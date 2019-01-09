package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.AccountRecord;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
@RequestScoped
public class AccountResource {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");

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
            //TODO: Probably should have a central EntityManager
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

    @Path("/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getOneAccount(@PathParam("accountId") final String accountId) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            AccountRecord ar = (AccountRecord) em.createQuery("Select a from AccountRecord a where a.id = '" + accountId + "'").getSingleResult();

            ObjectMapper om = new ObjectMapper();
            return om.writeValueAsString(ar);
        }
        catch(JsonProcessingException jpe) {
            System.out.println("Exception processing JSON: " + jpe.getMessage());
            return "Error: " + jpe.getMessage();
        }
        catch(NoResultException nre) {
            //TODO: Probably shouldn't rely on catching an error here and handle this smoother
            return null;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Path("/{accountId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public int deleteAccount(@PathParam("accountId") final String accountId) {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from AccountRecord a where a.id = '" + accountId + "'").executeUpdate();
        em.getTransaction().commit();

        return returnVal;
    }

    @Path("")
    @DELETE
    public int deleteAll() {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from AccountRecord a").executeUpdate();
        em.getTransaction().commit();

        return returnVal;
    }
}