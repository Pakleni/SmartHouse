/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import resources.smarthouse.Users;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author pakleni
 */
@Provider
public class BasicAuthFilter implements ContainerRequestFilter{

    @PersistenceContext(unitName = "UserServicePU")
    EntityManager em;
    
    public static Users get_user(ContainerRequestContext requestContext, EntityManager em) {
        
        List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");
        
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            
            List<Users> users = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", username).getResultList();
            
            if(users.size() != 1){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("No user with given username").build();
                requestContext.abortWith(response);
                return null;
            }
            
            Users user = users.get(0);
            
            if(!user.getPassword().equals(password)){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Password incorrect").build();
                requestContext.abortWith(response);
                return null;
            }
            
            return user;
            
        }
        Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Send creds").build();
        requestContext.abortWith(response);
        return null;
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        //GET
//        String method = requestContext.getMethod();
        UriInfo uriInfo = requestContext.getUriInfo();
//        String uriPath = requestContext.getUriInfo().getPath();
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        //speaker
        String endpointName = pathSegments.get(0).getPath();
        //       /play
        String pathSegment1 = null;
        if(pathSegments.size() > 1) pathSegment1 = pathSegments.get(1).getPath();
        
        if (endpointName.equals("user") && pathSegment1 != null && pathSegment1.equals("new")) return;
        
        Users user = get_user(requestContext, em);
    }
}
