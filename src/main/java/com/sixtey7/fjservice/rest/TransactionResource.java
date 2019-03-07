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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

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
    /**
     * REST Service used to import transactions for an account
     * @param accountId The id of the account to assign the transactions to
     * @param transactionData The data to import (comma separated)
     * @return {@link Response} containing the number of transactions imported
     */
    @Path("/import/{accountId}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importTransactions(@PathParam("accountId") final String accountId, final String transactionData) {
        /* Expected Order:
        0 - Name
        1 - Debit
        2 - Credit
        3 - Date
        4 - Notes
         */

        //Create a simple date format to help parse our date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        System.out.println("Importing transactions for account: " + accountId);

        String[] allLines = transactionData.split("\\n");
        System.out.println("Found " + allLines.length + " lines!");


        for (int lineCounter = 1; lineCounter < allLines.length; lineCounter++) {
            String[] lineData = allLines[lineCounter].split(",", 5);
            if (lineData.length == 5) {
                System.out.println("------------------------------------------------------------------------------------");
                System.out.println("Name: " + lineData[0]);
                System.out.println("Debit: " + lineData[1]);
                System.out.println("Credit: " + lineData[2]);
                System.out.println("Date: " + lineData[3]);
                System.out.println("Notes: " + lineData[4]);

                String name = lineData[0];
                float amount = 0;
                if (!lineData[1].equals("")) {
                    String value = lineData[1];
                    if (value.charAt(0) == '$')
                    {
                        value = value.substring(1);
                    }
                    amount = -1 * Float.parseFloat(value);
                }
                else if (!lineData[2].equals("")) {
                    String value = lineData[2];
                    if (value.charAt(0) == '$')
                    {
                        value = value.substring(1);
                    }
                    amount = Float.parseFloat(value);
                }
                else {
                    System.out.println("Failed to parse an amount 1: " + lineData[1] + " 2: " + lineData[2]);
                }

                Instant transDate = Instant.now();


                if (!lineData[3].equals("")) {
                    System.out.println("Got the time: " + lineData[3]);
                    try {
                        transDate = sdf.parse(lineData[3]).toInstant();
                    }
                    catch (ParseException pe) {
                        System.out.println("Failed to parse date: " + lineData[3]);
                    }
                }


                String notes = lineData[4];

                System.out.println("    ~~~~~");
                System.out.println("Name: " + name);
                System.out.println("Amount: " + amount);
                System.out.println("Date: " + transDate);
                System.out.println("Notes: " + notes);


                //TODO: This method really needs to take in a real UUID not an int
                Transaction newTransaction = new Transaction(name, transDate, amount, UUID.randomUUID(), notes);

                System.out.println("------------------------------------------------------------------------------------");

            }
            else {
                System.out.println("Line Data has: " + lineData.length + " lines...");
            }
        }


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
