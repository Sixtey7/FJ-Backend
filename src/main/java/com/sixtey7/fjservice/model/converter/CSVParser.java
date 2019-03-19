package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Class used to parse out transaction data from a CSV String
 */
public class CSVParser {

    /**
     * LOGGER for the class
     */
    private static final Logger LOGGER = LogManager.getLogger(CSVParser.class);

    /**
     * Leads the parsing of the CSV Data
     * @param textFromCSV the text from the CSV
     * @param accountUUID A {@link UUID} for the account the transactions belong to
     * @return {@link List} of {@link Transaction} containing the parsed transaction
     */
    public List<Transaction> parseCSVFile(String textFromCSV, UUID accountUUID) {
        LOGGER.info("Parsing a CSV File for import!");
        List<Transaction> returnList = new ArrayList<>();

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


        String[] allLines = textFromCSV.split("\\n");
        LOGGER.info("Found " + allLines.length + " lines!");

        for (int lineCounter = 1; lineCounter < allLines.length; lineCounter++) {
            String[] lineData = allLines[lineCounter].split(",", 5);
            if (lineData.length == 5) {
                LOGGER.debug("------------------------------------------------------------------------------------");
                LOGGER.debug("Name: {}", lineData[0]);
                LOGGER.debug("Debit: {}", lineData[1]);
                LOGGER.debug("Credit: {}", lineData[2]);
                LOGGER.debug("Date: {}", lineData[3]);
                LOGGER.debug("Notes: {}", lineData[4]);

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
                    LOGGER.warn("Failed to parse an amount 1: {} 2: {}", lineData[1], lineData[2]);
                }

                Instant transDate = Instant.now();


                if (!lineData[3].equals("")) {
                    LOGGER.debug("Got the time: {}", lineData[3]);
                    try {
                        transDate = sdf.parse(lineData[3]).toInstant();
                    }
                    catch (ParseException pe) {
                        LOGGER.error("Failed to parse date: {}", lineData[3], pe);
                    }
                }


                String notes = lineData[4];

                Transaction.TransType type = determineTransType(transDate, notes);

                //Note: I'm just assigning the transaction to an variable to log it below
                Transaction newTrans = new Transaction(name, transDate, amount, accountUUID, notes, type);
                returnList.add(newTrans);

                LOGGER.debug("    ~~~~~");
                LOGGER.debug(newTrans.toString());
                LOGGER.debug("    ~~~~~");

                LOGGER.debug("------------------------------------------------------------------------------------");

            }
            else {
                LOGGER.warn("Line Data has: " + lineData.length + " lines...");
            }
        }

        return returnList;
    }

    /**
     * Determines the type of transaction being imported based on the date and comments in the notes
     * @param transDate the date of the transaction as an {@link Instant}
     * @param notesField the value of the notes field
     * @return {@link Transaction.TransType} value for the transaction
     */
    private Transaction.TransType determineTransType(Instant transDate, String notesField) {
        LOGGER.debug("Determining type for date: {} and notesField {}", transDate, notesField);
        //Start by seeing if the transaction is in the past
        if (transDate.isBefore(Instant.now())) {
            LOGGER.debug("Determined CONFIRMED");
            return Transaction.TransType.CONFIRMED;
        }

        if (notesField.contains("est")) {
            LOGGER.debug("Determined ESTIMATE");
            return Transaction.TransType.ESTIMATE;
        }

        if (notesField.contains("planned")) {
            LOGGER.debug("Determined PLANNED");
            return Transaction.TransType.PLANNED;
        }

        LOGGER.debug("Determined FUTURE");
        return Transaction.TransType.FUTURE;
    }
}
