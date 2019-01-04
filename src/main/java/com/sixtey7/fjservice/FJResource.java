package com.sixtey7.fjservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.AccountRecord;
import org.glassfish.json.JsonParserImpl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

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

    @Path("/tryDatabase")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String tryDatabase() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");
            EntityManager em = emf.createEntityManager();

            List<AccountRecord> records = em.createQuery("Select a from AccountRecord a", AccountRecord.class).getResultList();

            em.close();
            if (records.size() > 0) {
                return new ObjectMapper().writeValueAsString(records.get(0).getData());
                //return records.get(0).getData();
            }
            else {
                return "No Results.";
                /*return Json.createObjectBuilder()
                        .add("message","no results")
                        .build();*/
            }
        }
        catch (Exception ex) {
            return ex.getMessage();
            /*return Json.createObjectBuilder()
                    .add("message", "Error Reading From Database")
                    .add("error", ex.getMessage())
                    .build();*/
        }
    }

    @Path("/putIntoDB/{message}")
    @PUT
    public void putIntoDB(@PathParam("message") final String message) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");
            EntityManager em = emf.createEntityManager();

            //JsonObject objectToStore  = Json.createObjectBuilder()
            //        .add("message", message)
            //        .build();


            Account objectToStore = new Account(message);
            AccountRecord ar = new AccountRecord(UUID.randomUUID(), objectToStore);

            em.getTransaction().begin();
            em.persist(ar);
            em.getTransaction().commit();;
            em.close();
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}