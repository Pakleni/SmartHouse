/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package speaker;

import SmartHouse.Planer;
import SmartHouse.PlayHistory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author pakleni
 */
public class SpeakerController {
    
   
    @Resource(lookup="jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup="SpeakerQ")
    private static Queue myQueue;
    
    @Resource(lookup="SpeakerResponseQ")
    private static Queue historyQueue;
    
    private static void sendHistory(int userID) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SpeakerPU");
        EntityManager em = emf.createEntityManager();
        JMSContext context = connectionFactory.createContext();
        
        TypedQuery query = em.createNamedQuery("PlayHistory.findByIdUsers", PlayHistory.class);
        
        query.setParameter("idUsers", userID);
        
        List<PlayHistory> hists = query.getResultList();
        
        JSONArray arr = new JSONArray();
        
        for (PlayHistory hist: hists) {
            String curr = hist.getQuery();
            
            arr.add(curr);
        }
        
        System.out.println(arr.toJSONString());

                    
        try {
            //JMSContext context=connectionFactory.createContext();
            //JMSConsumer consumer=context.createConsumer(myQueue, "userID="+userID);

            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            String history = arr.toJSONString(); 
            
            msg.setText(history);
            msg.setIntProperty("userID", userID);
            producer.send(historyQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void saveToHistory(int userID, String query) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SpeakerPU");
        EntityManager em = emf.createEntityManager();

        
        try{
            EntityTransaction transaction = em.getTransaction();

            PlayHistory hist = new PlayHistory(); //stanje new
            hist.setIdUsers(userID);
            hist.setQuery(query);
            
            transaction.begin();

            em.persist(hist); //prelazi iz stanja new u managed

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
                    
                    if (obj.containsKey("user")) {
                        int userID = ((Long)obj.get("user")).intValue();
                        
                        if (obj.containsKey("query")) {
                            String query = (String)obj.get("query");

                            Player.searchAndPlay(query);

                            saveToHistory(userID, query);
                            
                            continue;
                        }
                        
                        sendHistory(userID);
                    }
                    
                } catch (JMSException ex) {
                    Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SpeakerController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
    
}
