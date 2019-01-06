package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.AccountRecord;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
@RequestScoped
public class AccountResource {

    @Path("/test")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject test() {
        return Json.createObjectBuilder()
                .add("message", "Account Service Up and Running!")
                .build();
    }

    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllAccounts() {
        //TODO: This should be factored out of the class
        EntityManager em = null;
        try {
            //TODO: Likely only need one emf per class, or per isntance
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(("FJDB"));
            em = emf.createEntityManager();

            List<AccountRecord> records = em.createQuery("Select a from AccountRecord a", AccountRecord.class).getResultList();
            //I believe OM's are expensive to create, should be smarter here
            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(records);
        }
        catch(JsonProcessingException jpe) {
            //TODO: do real logging
            System.out.println("Exception processing JSON" + jpe.getMessage());
            return "Error: " + jpe.getMessage();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }
}