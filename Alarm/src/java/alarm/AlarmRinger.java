/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import smarthouse.Alarm;
import smarthouse.AlarmSound;
import smarthouse.DatedAlarm;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.json.simple.JSONObject;


/**
 *
 * @author pakleni
 */
public class AlarmRinger extends Thread{
    
    protected AlarmRinger(){}
    
    private static AlarmRinger singleton = null;
    
    public static AlarmRinger getInstance(){
        if (singleton == null) {
            singleton = new AlarmRinger();
        }
        
        return singleton;
    }
    
    private void ring (int user) {
        AlarmSound sound = em.find(AlarmSound.class, user);
        
        if (sound == null) {
            ring (user, "despacito");
            return;
        }
        
        ring (user, sound.getQuery());
    }
    
    private void ring(int user, String query) {
        try {
            JMSContext context= AlarmController.connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            
            obj.put("user", user);
            obj.put("query", query);
            
            
            msg.setText(obj.toJSONString());
            producer.send(AlarmController.zvukQ, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(AlarmController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void executeAlarms() {
        LocalTime time = java.time.LocalTime.now();
        
        int h = time.getHour();
        int m = time.getMinute();
        
        int total = h * 60 + m;
        
        TypedQuery<Alarm> query = em.createNamedQuery("Alarm.findByTime", Alarm.class);
        
        query.setParameter("time", total);
        
        List<Alarm> alarms = query.getResultList();
        
        for (Alarm alarm: alarms) {
            ring(alarm.getIdUsers());
        }
        
        try{
            
            EntityTransaction transaction = em.getTransaction();
        
            transaction.begin();

            for (Alarm alarm: alarms) {
                if (alarm.getPeriodic() == AlarmController.AlarmStatus.ON.ordinal()){
                    em.remove(alarm);
                }
            }

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
        }
    }
    
    private void executeDatedAlarms() {
        long millis=System.currentTimeMillis() / (60 * 1000) * (60 * 1000);
        
        java.util.Date date=new java.util.Date(millis);
        
        
        TypedQuery<DatedAlarm> query = em.createNamedQuery("DatedAlarm.findByDatetime", DatedAlarm.class);
        
        query.setParameter("datetime", date);
        
        List<DatedAlarm> alarms = query.getResultList();
        
        for (DatedAlarm alarm: alarms) {
            ring(alarm.getIdUsers());
        }
        
        try{
            
            EntityTransaction transaction = em.getTransaction();
        
            transaction.begin();

            for (DatedAlarm alarm: alarms) {
                em.remove(alarm);
            }

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
        }
        
        
    }
    
    private EntityManagerFactory emf;

    private EntityManager em;
    
    @Override
    public void run() {
        emf = Persistence.createEntityManagerFactory("AlarmPU");
        em = emf.createEntityManager();
                
        while (!Thread.interrupted()) {
            executeAlarms();
            executeDatedAlarms();
            
            try {
                Thread.sleep(60001 - (System.currentTimeMillis()) % 60000);
            } catch (InterruptedException ex) {
            }
        }
        
        
        em.close();
        emf.close();
    }
    
    public static void main(String[] args) {
        AlarmRinger.getInstance().start();
    }
}
