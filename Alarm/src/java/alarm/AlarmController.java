 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alarm;

import smarthouse.Alarm;
import smarthouse.AlarmSound;
import smarthouse.DatedAlarm;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pakleni
 */
public class AlarmController {
    
    public enum AlarmStatus {
        OFF,
        ON,
        PERIODIC
      }
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup="AlarmQ")
    private static Queue myQueue;  
    
    public static void setDated(JSONObject jo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
        
        DatedAlarm planerAlarm = new DatedAlarm();
        
        planerAlarm.setDatetime(new Date((long)jo.get("time")));
        planerAlarm.setIdUsers(((Long)jo.get("user")).intValue());
        
        try{
            EntityTransaction transaction = em.getTransaction();
        
            transaction.begin();

            em.persist(planerAlarm);

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            
            em.close();
            emf.close();
        }
    }
    
    public static void set(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();
        
        try{
            EntityTransaction transaction = em.getTransaction();

            Alarm alarm = new Alarm(); //stanje new
            
            alarm.setIdUsers(((Long)obj.get("user")).intValue());
            
            if (obj.containsKey("periodic") && (boolean)obj.get("periodic"))
                alarm.setOn(AlarmStatus.PERIODIC.ordinal());
            else 
                alarm.setOn(AlarmStatus.ON.ordinal());
            
            alarm.setTime(((Long)obj.get("time")).intValue());
            
            transaction.begin();

            em.persist(alarm); //prelazi iz stanja new u managed

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            
            em.close();
            emf.close();
        }
    }
    
    public static void config(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();
        
        int id = ((Long)obj.get("user")).intValue();
        
        AlarmSound sound = em.find(AlarmSound.class, id);
        
        if (sound == null) {
            sound = new AlarmSound(); //stanje new
            
            sound.setIdUsers(id);
            
        }
        
        sound.setQuery((String)obj.get("query"));

        try{
            EntityTransaction transaction = em.getTransaction();

            
            
            transaction.begin();

            em.persist(sound); //prelazi iz stanja new u managed

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            
            em.close();
            emf.close();
        }
    }
    
    public static void main(String[] args) {
        
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer=context.createConsumer(myQueue);
        
        JSONParser parser = new JSONParser();
        
        while(true){
            Message msg=consumer.receive();
            if(msg instanceof TextMessage){
                try {
                    TextMessage txtMsg=(TextMessage)msg;
                    String txt = txtMsg.getText();
                    
                    JSONObject obj = (JSONObject) parser.parse(txt);
                    
                    if (obj.containsKey("user") && obj.containsKey("type")){
                        switch((String)obj.get("type")) {
                            case "set":
                                set(obj);
                                break;
                            case "dated":
                                setDated(obj);
                                break;
                            case "config":
                                config(obj);
                                break;
                        }
                    }
                    
                } catch (JMSException ex) {
                    Logger.getLogger(AlarmController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(AlarmController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
