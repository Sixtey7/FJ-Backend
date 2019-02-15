package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.TransactionDAO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * RESTful Service used to creat and obtain data about transactions
 */
@Path("/transactions")
@RequestScoped
public class TransactionResource {

    /**
     * DAO object to be used to access the daatabase
     */
    @Inject
    private TransactionDAO  dao;

    /**
     * REST service used to verify the Transaction Service is up and running
     * @return {@link JsonObject} indicating the status of the service
     */
    @Path("/healthz")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject healthz() {
        return Json.createObjectBuilder()
                .add("message", "Transaction Service Up and Running!")
                .build();
    }

    /**
     * REST Service used to obtain all of the transactions in the database
     * @return {@link Response} that contains all of the transaactions
     */
    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions() {
        List<Transaction> allTransactions = dao.getAllTransactions();

        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(allTransactions);

            return Response.status(200).entity(returnString).build();
        }
        catch(JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    /**
     * REST Service used to obtain the details of a single transaction
     * @param transId String containing the UUID of the transaction to get
     * @return {@link Response} containing the details of the requested transaction
     */
    @Path("/{transId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneTransaction(@PathParam("transId") final String transId) {
        try {
            Transaction transaction = dao.getTransaction(transId);

            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(transaction);

            return Response.status(200).entity(returnString).build();
        }
        catch(JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    /**
     * REST Service used to get all of the transactions tied to the specified account
     * @param accountId String containing the UUID of the account to get transactions for
     * @return {@link Response} containing the list of matching transactions
     */
    @Path("/forAccount/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransForAccount(@PathParam("accountId") final String accountId) {
        List allTransactions = dao.getTransForAccount(accountId);

        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(allTransactions);

            return Response.status(200).entity(returnString).build();
        }
        catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    /**
     * REST Service used to add a transaction to the database
     * @param transaction The Details of the transaction to be deserialized into a {@link Transaction}
     * @return {@link Response} containing the UUID of the newly created Transaction record
     */
    @Path("")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTransaction(Transaction transaction) {
        if (transaction.getTransId() != null) {
            return Response.status(400).entity("Use POST method if updating").build();
        }

        String newId = dao.addTransaction(transaction);

        return Response.status(200).entity(newId).build();
    }

    @Path("/import/{accountId}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importTransactions(@PathParam("accountId") final String accountId, final String transactionData) {
        return Response.status(200).build();
    }

    /**
     * REST Service used to update a transaction
     * @param transactionId String containing the UUID of the transaction to update
     * @param transaction The details of the transaction to be deserialized into a {@link Transaction}
     * @return {@link Response} containing whether the update was successful or not
     */
    @Path("/{transactionId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("accountId") final String transactionId, final Transaction transaction) {
        if (transactionId == null) {
            return Response.status(400).entity("Transaction ID is required as part of the path!").build();
        }

        boolean result = dao.updateTransaction(transactionId, transaction);

        if (result) {
            return Response.status(200).build();
        }
        else {
            return Response.status(200).entity("Failed to save transaction update!").build();
        }
    }

    /**
     * REST Service used to delete a transaction from the database
     * @param transactionId String containing the UUID of the transaction to be deleted
     * @return {@link Response} containing the number of records deleted
     */
    @Path("/{transactionId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTransaction(final String transactionId) {
        int response = dao.deleteTransaction(transactionId);

        return Response.status(200).entity(response).build();
    }

    /**
     * REST Service used to delete all transactions from the database
     * @return {@link Response} containing the number of records deleted
     */
    @Path("")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllTransactions() {
        int response = dao.deleteAllTransactions();

        return Response.status(200).entity(response).build();
    }
}
