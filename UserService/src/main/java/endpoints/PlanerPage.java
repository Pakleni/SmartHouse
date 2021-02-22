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
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import resources.smarthouse.Users;

/**
 *
 * @author pakleni
 */
@Path("planer")
public class PlanerPage {
    
    @PersistenceContext(unitName = "UserServicePU")
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup="PlanerQ")
    private Queue myQueue;
    
    @Resource(lookup="PlanerResponseQ")
    private Queue responseQueue;
    
    @GET
    @Path("create")
    public Response Create(ContainerRequestContext requestContext, @QueryParam("duration") int duration,
                             @QueryParam("name") String name,
                             @QueryParam("location") String location,
                             @QueryParam("time") long time){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer= context.createConsumer(responseQueue, "userID = " + userID);
        
        try {
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("type", "C");
            obj.put("user", userID);
            obj.put("duration", duration);
            obj.put("name", name);
            obj.put("time", time);
            
            if (!location.equals("")) {
                obj.put("location", location);
            }
            
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
    
    @GET
    @Path("read")
    public Response Read(ContainerRequestContext requestContext){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer= context.createConsumer(responseQueue, "userID = " + userID);
        
        try {
            
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("type", "R");
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
    
    @GET
    @Path("update")
    public Response Update(ContainerRequestContext requestContext, @QueryParam("id") int id,
                           @QueryParam("duration") int duration,
                           @QueryParam("name") String name,
                           @QueryParam("location") String location,
                           @QueryParam("time") long time){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        JMSConsumer consumer= context.createConsumer(responseQueue, "userID = " + userID);
        
        try {
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("type", "U");
            obj.put("user", userID);
            obj.put("id", id);
            
            if (duration > -1) {
                obj.put("duration", duration);
            }
            if (!name.equals("")){
                obj.put("name", name);
            }
            if (time > -1) {
                obj.put("time", time);
            }
            if (!location.equals("")) {
                obj.put("location", location);
            }
            
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
    
    @GET
    @Path("delete")
    public Response Delete(ContainerRequestContext requestContext, @QueryParam("id") int id){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        
        try {
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("type", "D");
            obj.put("user", userID);
            obj.put("id", id);
            
            String text = obj.toJSONString(); 
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("alarm")
    public Response Alarm(ContainerRequestContext requestContext, @QueryParam("id") int id){
        
        Users user = BasicAuthFilter.get_user(requestContext, em);
        
        if (user == null) {return null;}
        
        int userID = user.getIdUsers();
        
        JMSContext context=connectionFactory.createContext();
        
        try {
            JMSProducer producer = context.createProducer();

            TextMessage msg=context.createTextMessage();
            
            JSONObject obj = new JSONObject();
            obj.put("type", "A");
            obj.put("user", userID);
            obj.put("id", id);
            
            String text = obj.toJSONString(); 
            
            
            msg.setText(text);
            producer.send(myQueue, msg);
            
        } catch (JMSException ex) {
            Logger.getLogger(SpeakerPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
}
