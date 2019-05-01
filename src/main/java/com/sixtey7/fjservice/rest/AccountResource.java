package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.db.AccountDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * Resource Class providing REST Interfaces for Accounts
 */
@Path("/accounts")
@RequestScoped
public class AccountResource {

    //Create a logger for the class
    private static final Logger LOGGER = LogManager.getLogger(AccountResource.class);

    /**
     * DAO Used to interact with the database
     */
    @Inject
    private AccountDAO dao;

    /**
     * Temporary interface used to verify resource is deployed correctly
     * @return {@link JsonObject} with the status of the resource
     */
    @Path("/healthz")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject test() {
        LOGGER.info("Returning Health Check");
        return Json.createObjectBuilder()
                .add("message", "Account Service Up and Running!")
                .build();
    }

    /**
     * REST Service to obtain all accounts in the database
     * @return {@link Response} object containing all of the accounts
     */
    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        LOGGER.info("Returning all accounts");
        List<Account> allAccounts = dao.getAllAccounts();

        LOGGER.debug("Returning {} accounts", allAccounts.size());
        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(allAccounts);

            return Response.status(200).entity(returnString).build();
        }
        catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }

    }

    /**
     * REST Service to obtain information about a single account
     * @param accountId Strike containing the UUID of the account to get data for
     * @return {@link Response} object containing the requested {@link Account} object
     */
    @Path("/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneAccount(@PathParam("accountId") final String accountId) {
        LOGGER.info("Returning account details for id {}", accountId);
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

    /**
     * REST Service used to add an account to the database
     * @param account {@link Account} to add
     * @return {@link Response} object containing the UUID of the added account
     */
    @Path("")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAccount(Account account) {
        LOGGER.info("Adding a new account");
        if (account.getId() != null) {
            LOGGER.warn("PUT method was called to add a new account for existing id {}", account.getId());
            return Response.status(400).entity("Use POST method if updating").build();
        }

        String newId = dao.addAccount(account);

        LOGGER.debug("Assigned ID {}", newId);

        return Response.status(200).entity(newId).build();
    }

    /**
     * REST Service used to update the indicated account
     * @param accountId String containing the UUID of the account to update
     * @param account {@link Account} the account object to store
     * @return {@link Response} object indicating if the update was successful
     */
    @Path("/{accountId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("accountId") final String accountId, final Account account) {
        LOGGER.info("Updating account info for account id: {}", account);
        if (accountId == null) {
            LOGGER.warn("Account id was not provided to POST method");
            return Response.status(400).entity("ID is required as part of the path!").build();
        }

        boolean result = dao.updateAccount(accountId, account);

        if (result) {
            return Response.status(200).build();
        }
        else {
            LOGGER.error("Failed to save the account update for account id {}", accountId);
            return Response.status(500).entity("Failed to save account update!").build();
        }

    }

    /**
     * REST Service used to delete the indicated account
     * @param accountId String containing the UUID of the account to delete
     * @return {@link Response} object containing the number of records deleted
     */
    @Path("/{accountId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAccount(@PathParam("accountId") final String accountId) {
        LOGGER.info("Deleting account with id: {}", accountId);
        int response = dao.deleteAccount(accountId);

        LOGGER.debug("Deleted {} accounts", response);
        return Response.status(200).entity(response).build();
    }

    /**
     * REST Service used to delete all of the accounts
     * @return {@link Response} object containing the number of records deleted
     */
    @Path("")
    @DELETE
    public Response deleteAll() {
        LOGGER.info("Deleting all accounts!");
        int response = dao.deleteAllAccounts();

        LOGGER.debug("Deleted {} accounts", response);
        return Response.status(200).entity(response).build();
    }
}