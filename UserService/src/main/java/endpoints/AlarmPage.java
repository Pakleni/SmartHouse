/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import filters.BasicAuthFilter;
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
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import resources.smarthouse.Users;

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
    
    @POST
    @Path("new")
    public Response SetAlarm(ContainerRequestContext requestContext,
                             @QueryParam("time") int time,
                             @QueryParam("periodic") int periodic){
        
        JMSContext context=connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        TextMessage msg=context.createTextMessage();
            
        JSONObject obj = new JSONObject();
        obj.put("user", userID);
        obj.put("time", time);
        if (periodic > 0) {
            obj.put("periodic", true);            
        }
        obj.put("type", "set");

        String text = obj.toJSONString(); 
            
        try {
            msg.setText(text);
            producer.send(myQueue, msg);
            
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(text).build();
        }
        
        return Response.status(Response.Status.OK).entity(text).build();
    }
    
    @POST
    @Path("song/{query}")
    public Response SetSong(ContainerRequestContext requestContext, @PathParam("query") String query){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        
        try {
            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("user", userID);
            obj.put("query", query);
            obj.put("type", "config");
            
            String text = obj.toJSONString();  
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
    
}
