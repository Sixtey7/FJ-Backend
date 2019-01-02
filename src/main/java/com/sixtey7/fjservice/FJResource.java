package com.sixtey7.fjservice;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
@RequestScoped
public class FJResource {
    
    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getDefaultMessage() {
        String msg = String.format("%s %s!", "Hello", "World");

        return Json.createObjectBuilder()
            .add("message", msg)
            .build();
    }

    @Path("/goodbyeWorld")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject goodbyeWorld() {
        String message = String.format("Goodbye World");

        return Json.createObjectBuilder()
                .add("message", message)
                .build();
    }
}