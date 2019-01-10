package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.AccountRecord;
import com.sixtey7.fjservice.model.db.AccountDAO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/accounts")
@RequestScoped
public class AccountResource {

    @Inject
    private AccountDAO dao;


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
    public Response getAllAccounts() {
        List<Account> allAccounts = dao.getAllAccounts();

        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(allAccounts);

            return Response.status(200).entity(returnString).build();
        }
        catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }

    }

    @Path("/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneAccount(@PathParam("accountId") final String accountId) {
        try {
            Account account = dao.getAccount(accountId);

            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(account);

            return Response.status(200).entity(returnString).build();
        }
        catch(JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    @Path("")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAccount(Account account) {
        if (account.getId() != null) {
            return Response.status(400).entity("Use POST method if updating").build();
        }

        String newId = dao.addAccount(account);
        
        return Response.status(200).entity(newId).build();
    }

    @Path("/{accountId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("accountId") final String accountId, final Account account) {
        if (accountId == null) {
            return Response.status(400).entity("ID is required as part of the path!").build();
        }

        boolean result = dao.updateAccount(accountId, account);

        if (result) {
            return Response.status(200).build();
        }
        else {
            return Response.status(500).entity("Failed to save account update!").build();
        }

    }

    @Path("/{accountId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public int deleteAccount(@PathParam("accountId") final String accountId) {
        return dao.deleteAccount(accountId);
    }

    @Path("")
    @DELETE
    public int deleteAll() {
        return dao.deleteAllAccounts();
    }
}