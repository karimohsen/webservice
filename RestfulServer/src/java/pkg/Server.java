/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg;

import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Karim
 */
@Path("/hello")
public class Server {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public String getMsg(@PathParam("name")String name) {
         return "name is : " + name;
    }

}
