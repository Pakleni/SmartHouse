package planer;

import SmartHouse.Planer;
import SmartHouse.DatedAlarm;
import SmartHouse.Users;
import java.util.Date;
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
public class PlanerController {
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup="PlanerQ")
    private static Queue myQueue;
    
    @Resource(lookup="PlanerResponseQ")
    private static Queue responseQueue;
    
    @Resource(lookup="AlarmQ")
    private static Queue alarmQ;
    
    private static int getInt(JSONObject obj, String key) {
        return ((Long)obj.get(key)).intValue();
    }
    
    private static boolean calcGoodTime (Planer p1, Planer p2, EntityManager em) {
        return (p1.getStart().getTime() + getOffset(p1, p2, em) + p1.getDuration() <= p2.getStart().getTime());
    }
    
    private static boolean isGood(Planer planer, EntityManager em) {
        Planer prev = getPreviousEngagement(planer, em);
        Planer next = getNextEngagement(planer, em);
        
        if (prev != null && !calcGoodTime (prev, planer, em)) {
            return false;
        }
        
        if (next != null && !calcGoodTime (planer, next, em)) {
            return false;
        }
        
        return true;
    }
    
    private static void respond (boolean good, int userID) {
        
        JSONObject obj = new JSONObject();
        
        obj.put("good", good);
        
        try {
            //JMSContext context=connectionFactory.createContext();
            //JMSConsumer consumer=context.createConsumer(myQueue, "userID="+userID);

            JMSContext context=connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            msg.setText(obj.toJSONString());
            
            msg.setIntProperty("userID", userID);
            producer.send(responseQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void create(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();

        Planer planer = new Planer(); //stanje new
        
        int userID = getInt(obj, "user");
        planer.setIdUsers(userID);

        planer.setDuration(getInt(obj, "duration"));

        planer.setName((String)obj.get("name"));

        Date date = new Date((long)obj.get("time"));

        planer.setStart(date);

        if (obj.containsKey("location")) {
            planer.setDestination((String)obj.get("location"));
        }
        
        boolean good = false;
        
        if (!isGood(planer, em)){
            try{
                EntityTransaction transaction = em.getTransaction();
                
                transaction.begin();

                em.persist(planer); //prelazi iz stanja new u managed

                transaction.commit();

            }finally{

                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                else good = true;
                
                em.close();
                emf.close();
            }
        }
        
        respond (good, userID);
    }
    
    private static void read(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();

        int userID = getInt(obj, "user");
        
        Date currDate = new Date();
        
        TypedQuery query = em.createQuery("SELECT p FROM Planer p WHERE p.idUsers = :idUsers AND p.start > :curr", Planer.class);
        
        query.setParameter("idUsers", userID);
        query.setParameter("curr", currDate);
        
        List<Planer> planeri = query.getResultList();
        
        JSONArray arr = new JSONArray();
        
        for (Planer planer: planeri) {
            JSONObject curr = new JSONObject();
            
            curr.put("id", planer.getIdPlanner());
            curr.put("time", planer.getStart().getTime());            
            curr.put("duration", planer.getDuration());
            
            if (planer.getDestination() != null) {
                curr.put("location", planer.getDestination());
            }
            
            arr.add(curr);
        }
        
        try {
            //JMSContext context=connectionFactory.createContext();
            //JMSConsumer consumer=context.createConsumer(myQueue, "userID="+userID);

            JMSContext context=connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            String response = arr.toJSONString(); 
            
            msg.setText(response);
            msg.setIntProperty("userID", userID);
            
            producer.send(responseQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static void update(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
        
        int id = getInt(obj, "id");
        int userID = getInt(obj, "user");
        
        
        Planer planer = em.find(Planer.class, id);
   
        
        if(planer == null || planer.getIdUsers() != userID) {
            respond (false, userID);
            return;
        }
        
        if (obj.containsKey("name")){
            planer.setName((String)obj.get("name"));
        }

        if (obj.containsKey("time")){
            planer.setStart(new Date((long)obj.get("time")));
        }

        if (obj.containsKey("duration")){
            planer.setDuration(getInt(obj, "duration"));
        }

        if (obj.containsKey("location")){
            planer.setDestination((String)obj.get("location"));
        }
        
        
        boolean good = false;
        
        if (!isGood(planer, em)){
            try{    
                EntityTransaction transaction = em.getTransaction();

                transaction.begin();

                em.persist(planer);

                transaction.commit();

            }finally{

                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                else good = true;
                
                em.close();
                emf.close();
            }
        }
        
        respond(good, userID);
    }
    
    private static void delete(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
        
        int id = getInt(obj, "id");
        int userID = getInt(obj, "user");
        
        
        Planer planer = em.find(Planer.class, id);
        
        if(planer == null)
            return;
        
        if(planer.getIdUsers() != userID)
            return;

        try{
            
            EntityTransaction transaction = em.getTransaction();
        
            transaction.begin();

            em.remove(planer);

            transaction.commit();

        }finally{

            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            
            em.close();
            emf.close();
        }
    }
    
    private static Planer getPreviousEngagement(Planer planer, EntityManager em) {
        
        String query = "SELECT p FROM Planer p WHERE p.start < :start AND p.idPlanner != :id ORDER by p.start DESC";
        
        TypedQuery<Planer> tq = em.createQuery(query, Planer.class);
        
        tq.setMaxResults(1);
        
        tq.setParameter("start", planer.getStart());
        tq.setParameter("id", planer.getIdPlanner());
        
        Planer p = null;
        try {
            p = tq.getSingleResult();
        }
        catch(javax.persistence.NoResultException e) {
            
        }
        
        return p;
    }
    
    private static Planer getNextEngagement(Planer planer, EntityManager em) {
        
        String query = "SELECT p FROM Planer p WHERE p.start > :start AND p.idPlanner != :id ORDER by p.start ASC";
        
        TypedQuery<Planer> tq = em.createQuery(query, Planer.class);
        
        tq.setMaxResults(1);
        
        tq.setParameter("start", planer.getStart());
        tq.setParameter("id", planer.getIdPlanner());
        
        Planer p = null;
        try {
            p = tq.getSingleResult();
        }
        catch(javax.persistence.NoResultException e) {
            
        }
        
        return p;
    }
    
    private static long getOffset(Planer planer, Planer prev, EntityManager em) {
        
        
        String pos1, pos2;
        
        if (prev == null || prev.getDestination() == null) {
            pos1 = null;
        }
        else {
            pos1 = prev.getDestination();
        }
        
        if (planer.getDestination() == null) {
            pos2 = null;
        }
        else {
            pos2 = planer.getDestination();
        }
        
        
        if ((pos1 == null && pos2 == null) || (pos1 != null && pos1.equals(pos2))) {
            return 0;
        }
        
        if (pos1 == null || pos2 == null) {
            
            Users user = em.find(Users.class, planer.getIdUsers());
            
            String home = user.getHome();
            
            if (pos1 == null) {
                pos1 = home;
            }

            if (pos2 == null) {
                pos2 = home;
            }
        }
        
        
        
        return DistanceCalculator.getTime(pos1, pos2) * 1000;
    }
    
    private static void alarm(JSONObject obj) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PlanerPU");
        EntityManager em = emf.createEntityManager();
        
        int id = getInt(obj, "id");
        int userId = getInt(obj, "user");
        
        Planer planer = em.find(Planer.class, id);
        
        if (planer == null) return;
        
        if (planer.getIdUsers() != userId ) return;
        
        JSONObject jo = new JSONObject();
        
        
        long time;
        
        Date date = planer.getStart();
        
        Planer prev = getPreviousEngagement(planer, em);
        
        time = (date.getTime() - getOffset(planer, prev,  em))/ (60 * 1000) * (60 * 1000);
       
        
        jo.put("time", time);
        jo.put("user", userId);
        jo.put("type", "dated");
        
        try {

            JMSContext context=connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            String history = jo.toJSONString(); 
            
            msg.setText(history);
            producer.send(alarmQ, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
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
                            case "C":
                                create(obj);
                                break;
                            case "R":
                                read(obj);
                                break;
                            case "U":
                                update(obj);
                                break;
                            case "D":
                                delete(obj);
                                break;
                            case "A":
                                alarm(obj);
                                break;
                        }
                    }
                    
                } catch (JMSException ex) {
                    Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(PlanerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}