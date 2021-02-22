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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import resources.smarthouse.Users;

/**
 *
 * @author pakleni
 */
@Path("speaker")
public class SpeakerPage {
    
    @PersistenceContext(unitName = "UserServicePU")
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="SpeakerQ")
    private Queue myQueue;
    
    @Resource(lookup="SpeakerResponseQ")
    private Queue historyQueue;
    
    @GET
    @Path("play/{query}")
    public Response PlaySong(ContainerRequestContext requestContext, @PathParam("query") String query){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        try {
            JMSContext context=connectionFactory.createContext();
            JMSProducer producer = context.createProducer();

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
    
    @GET
    @Path("history")
    public Response GetHistory(ContainerRequestContext requestContext){
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer=context.createConsumer(historyQueue, "userID = " + userID);
        
        try {
            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("user", userID);
            
            String text = obj.toJSONString();  
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        Message msg=consumer.receive();
        
        if(msg instanceof TextMessage){
            try {
                TextMessage txtMsg=(TextMessage)msg;
                String txt = txtMsg.getText();
                
                return Response.status(Response.Status.OK).entity(txt).build();
            
            } catch (JMSException ex) {
                Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    
}
