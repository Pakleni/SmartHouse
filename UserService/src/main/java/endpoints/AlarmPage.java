/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

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
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author pakleni
 */
@Path("alarm")
public class AlarmPage {
    
    @PersistenceContext(unitName = "UserServicePU")
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="AlarmQ")
    private Queue myQueue;
    
    @GET
    @Path("new")
    public Response SetAlarm(@QueryParam("time") int time,
                             @QueryParam("periodic") int periodic){
        
        int userID = 1;
        
        try {
            JMSContext context=connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("user", userID);
            obj.put("time", time);
            if (periodic > 0) {
                obj.put("periodic", true);            
            }
            
            String text = obj.toJSONString(); 
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("song/{query}")
    public Response SetSong(@PathParam("query") String query){
        int userID = 1;
        
        JMSContext context=connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        try {
            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("user", userID);
            obj.put("query", query);
            
            String text = obj.toJSONString();  
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
    
}
