package com.sixtey7.fjservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.AccountRecord;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.*;
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
            }
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Path("/putIntoDB/{message}")
    @PUT
    public void putIntoDB(@PathParam("message") final String message) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");
            EntityManager em = emf.createEntityManager();

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