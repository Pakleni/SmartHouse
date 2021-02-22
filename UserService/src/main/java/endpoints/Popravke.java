package endpoints;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 *
 * @author Stefan
 */
@Path("popravke")
@Stateless
public class Popravke {
    
    @PersistenceContext(unitName = "UserServicePU")
    EntityManager em;
    
    @GET
    @Path("kme")
    public Response kme(){
        return Response.status(Response.Status.OK).entity("Kme.").build();
    }
    
    
}
