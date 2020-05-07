package com.sixtey7.fjservice.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.converter.CSVGenerator;
import com.sixtey7.fjservice.model.converter.CSVParser;
import com.sixtey7.fjservice.model.converter.LegacyCSVParser;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.model.transport.TxUpdate;
import com.sixtey7.fjservice.utils.AccountHelper;
import com.sixtey7.fjservice.utils.TransHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RESTful Service used to create and obtain data about transactions
 */
@Path("/transactions")
@RequestScoped
public class TransactionResource {

    //create a logger for the class
    private static final Logger LOGGER = LogManager.getLogger(TransactionResource.class);

    /**
     * DAO object to be used to access the database
     */
    @Inject
    private TransactionDAO  dao;

    /**
     * DAO object for accounts
     */
    @Inject
    private AccountDAO acctDao;

    /**
     * Helper class used to manage accounts
     */
    @Inject
    private AccountHelper acctHelper;

    /**
     * Helper class used to massage transactions
     */
    @Inject
    private TransHelper transHelper;

    /**
     * Helper class used to generate csv data
     */
    @Inject
    private CSVGenerator csvGenerator;

    /**
     * Helper class used to import csv data
     */
    @Inject
    private CSVParser csvParser;

    /**
     * REST service used to verify the Transaction Service is up and running
     * @return {@link JsonObject} indicating the status of the service
     */
    @Path("/healthz")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject healthz() {
        LOGGER.info("Returning Health Check");

        return Json.createObjectBuilder()
                .add("message", "Transaction Service Up and Running!")
                .build();
    }

    /**
     * REST Service used to obtain all of the transactions in the database
     * @return {@link Response} that contains all of the transactions
     */
    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions() {
        LOGGER.info("Returning all transactions");
        List<Transaction> allTransactions = dao.getAllTransactions();

        LOGGER.debug("Got {} transactions", allTransactions.size());
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
     * @param transId {@link String} containing the UUID of the transaction to get
     * @return {@link Response} containing the details of the requested transaction
     */
    @Path("/{transId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOneTransaction(@PathParam("transId") final String transId) {
        LOGGER.info("Returning transaction details for id {}", transId);
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
     * @param accountId {@link String} containing the UUID of the account to get transactions for
     * @return {@link Response} containing the list of matching transactions
     */
    @Path("/forAccount/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransForAccount(@PathParam("accountId") final String accountId) {
        LOGGER.info("Getting the transaction details for account id {}", accountId);
        List allTransactions = dao.getTxForAccount(accountId);

        LOGGER.debug("Found {} transactions for account {}", allTransactions.size(), accountId);
        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(allTransactions);

            return Response.status(200).entity(returnString).build();
        }
        catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    @Path("betweenDates/{startDate}/{endDate}")
    @GET
    public Response getTransactionsBetweenDates(@PathParam("startDate") final String startDateStr, @PathParam("endDate") final String endDateStr) {
        LOGGER.debug("Getting the transactions between {} and {}", startDateStr, endDateStr);

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        List<Transaction> txsBetweenDates = dao.getTxBetweenDates(startDate, endDate);

        LOGGER.debug("Found {} transactions between the dates", txsBetweenDates.size());
        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(txsBetweenDates);

            return Response.status(200).entity(returnString).build();
        }
        catch (JsonProcessingException jpe) {
            return Response.status(500).entity(jpe.getMessage()).build();
        }
    }

    @Path("newerThan/{dateFilter}")
    @GET
    public Response getTransactionsNewerThan(@PathParam("dateFilter") final String dateFilterStr) {
        LOGGER.debug("Getting transactions newer than {}", dateFilterStr);

        LocalDate dateFilter = LocalDate.parse(dateFilterStr);

        List<Transaction> txNewerThan = dao.getTxNewerThan(dateFilter);

        LOGGER.debug("Found {} transactions newer than {}", txNewerThan.size(), dateFilterStr);

        try {
            ObjectMapper om = new ObjectMapper();
            String returnString = om.writeValueAsString(txNewerThan);

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
        LOGGER.info("Adding a new transaction!");

        if (transaction.getId() != null) {
            LOGGER.warn("PUT method was called to add a new transaction for existing id {}", transaction.getId());
            return Response.status(400).entity("Use POST method if updating").build();
        }

        //going to get an iso date from the frontend - fix it
        transHelper.fixDateForTrans(transaction);

        String newId = dao.addTransaction(transaction);
        LOGGER.debug("Assigned ID {}", newId);

        transaction.setId(UUID.fromString(newId));

        Account updatedAccount = acctHelper.updateBalanceForAccount(transaction.getAccountId());

        TxUpdate returnObject = new TxUpdate();
        returnObject.getAccounts().add(updatedAccount);
        returnObject.getTransactions().add(transaction);
        returnObject.setSuccess(true);
        return Response.status(200).entity(returnObject).build();

    }
    /**
     * REST Service used to import transactions for an account
     * @param accountId The id of the account to assign the transactions to
     * @param transactionData The data to import (comma separated)
     * @return {@link Response} containing the number of transactions imported
     */
    @Path("/legacyImport/{accountId}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importTransactions(@PathParam("accountId") final String accountId, final String transactionData) {
        LOGGER.info("Importing legacy transaction data for account id {}", accountId);
        //parse the account id
        UUID accountUUID = UUID.fromString(accountId);

        LOGGER.debug("Importing transactions for account: " + accountId);

        List<Transaction> transToImport = new LegacyCSVParser().parseCSVFile(transactionData, accountUUID);

        LOGGER.debug("Found {} transactions", transToImport.size());

        dao.addAllTransactions(transToImport);

        return Response.status(200).entity(transToImport.size()).build();
    }

    /**
     * REST Service used to import transactions into a new account
     * @param transactionData The data to import (comma separated)
     * @return {@link Response} containing the UUID of the created account
     */
    @Path("/legacyImport")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importLegacyTransactionsForNewAccount(final String transactionData) {
        LOGGER.info("Importing legacy transactions data for new account");

        Account newAccount = new Account("Imported");
        newAccount.setDynamic(false);
        newAccount.setName("Imported");
        newAccount.setNotes("Imported from a CSV Party");

        List<Transaction> transToImport = new LegacyCSVParser().parseCSVFile(transactionData, newAccount.getId());

        LOGGER.debug("Found {} transactions", transToImport.size());

        dao.addAllTransactions(transToImport);

        acctHelper.updateBalanceForAccount(newAccount);

        acctDao.updateAccount(newAccount);

        return Response.status(200).entity(newAccount.getId().toString()).build();
    }

    /**
     * Creates a CSV File with the data from the transactions
     * @return A {@link Response} containing the text from all {@link Transaction}
     */
    @Path("/csvFile")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateCSVFile() {
        LOGGER.info("Generating a CSV File for all transactions");

        String returnData = csvGenerator.generateCSVForAllTxs();

        return Response.status(200).entity(returnData).build();
    }

    /**
     * Imports the data from an exported CSV File
     * @param csvData Text from the CSV File
     * @return {@link Response} containing a {@link List} of {@link Transaction} parsed from the CSV File
     */
    @Path("/import")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response importFromCSV(String csvData) {
        LOGGER.info("importing accounts and transactions");

        List<Account> allAccounts = acctDao.getAllAccounts();
        Map<String, UUID> acctMap = acctHelper.buildNameToUUIDMap(allAccounts);

        try {
            List<Transaction> returnData = csvParser.parseAndClearAndStoreTxFromCSV(csvData, acctMap);

            return Response.status(200).entity(returnData).build();
        }
        catch (IllegalArgumentException iae) {
            return Response.status(400).entity(iae.getMessage()).build();
        }
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
    public Response updateAccount(Transaction transaction, @PathParam("transactionId") final String transactionId) {
        LOGGER.info("Updating transaction info for transaction id: {}" , transactionId);
        if (transactionId == null) {
            LOGGER.warn("Transaction id was not provided to POST method!");
            return Response.status(400).entity("Transaction ID is required as part of the path!").build();
        }


        LOGGER.debug("Received transaction details\n{}", transaction.toString());

        //going to get an ISO date from the frontend, let's fix that
        transHelper.fixDateForTrans(transaction);

        boolean result = dao.updateTransaction(transaction);

        //TODO: This really needs to be smarter, needs to look at old vs new
        // and only update if needed (if the state was or is now confirmed
        // and handle the case where an tx was moved between accounts
        Account updatedAccount = acctHelper.updateBalanceForAccount(transaction.getAccountId());

        if (result) {
            TxUpdate returnObject = new TxUpdate();
            returnObject.getAccounts().add(updatedAccount);
            returnObject.getTransactions().add(transaction);
            returnObject.setSuccess(result);
            return Response.status(200).entity(returnObject).build();
        }
        else {
            LOGGER.error("Failed to save the transaction update for transaction id {}", transactionId);
            return Response.status(500).entity("Failed to save transaction update!").build();
        }
    }

    /**
     * REST Service used to delete a transaction from the database
     * @param transactionId {@link String} containing the UUID of the transaction to be deleted
     * @return {@link Response} containing the number of records deleted
     */
    @Path("/{transactionId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteTransaction(@PathParam("transactionId") final String transactionId) {
        LOGGER.info("Deleting transaction with id: {}", transactionId);
        int response = dao.deleteTransaction(transactionId);

        LOGGER.debug("Deleting {} transactions", response);
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
        LOGGER.info("Deleting all transactions!");
        int response = dao.deleteAllTransactions();

        LOGGER.debug("Deleting {} transactions", response);

        return Response.status(200).entity(response).build();
    }
}
