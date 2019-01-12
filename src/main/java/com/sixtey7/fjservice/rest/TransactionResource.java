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

@Path("/transactions")
@RequestScoped
public class TransactionResource {

    @Inject
    private TransactionDAO  dao;

    @Path("/healthz")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject healthz() {
        return Json.createObjectBuilder()
                .add("message", "Transaction Service Up and Running!")
                .build();
    }

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

    @Path("/{transactionId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTransaction(final String transactionId) {
        int response = dao.deleteTransaction(transactionId);

        return Response.status(200).entity(response).build();
    }

    @Path("")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllTransactions() {
        int response = dao.deleteAllTransactions();

        return Response.status(200).entity(response).build();
    }
}
