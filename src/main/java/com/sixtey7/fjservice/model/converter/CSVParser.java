package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Transaction;

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
     * Leads the parsing of the CSV Data
     * @param textFromCSV the text from the CSV
     * @param accountUUID A {@link UUID} for the account the transactions belong to
     * @return {@link List} of {@link Transaction} containing the parsed transaction
     */
    public List<Transaction> parseCSVFile(String textFromCSV, UUID accountUUID) {
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
        System.out.println("Found " + allLines.length + " lines!");

        List<Transaction> transToImport = new ArrayList<>();
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

                Transaction.TransType type = determineTransType(transDate, notes);

                System.out.println("    ~~~~~");
                System.out.println("Name: " + name);
                System.out.println("Amount: " + amount);
                System.out.println("Date: " + transDate);
                System.out.println("Notes: " + notes);

                returnList.add(new Transaction(name, transDate, amount, accountUUID, notes, type));


                System.out.println("------------------------------------------------------------------------------------");

            }
            else {
                System.out.println("Line Data has: " + lineData.length + " lines...");
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
        //Start by seeing if the transaction is in the past
        if (transDate.isBefore(Instant.now())) {
            return Transaction.TransType.CONFIRMED;
        }

        if (notesField.contains("est")) {
            return Transaction.TransType.ESTIMATE;
        }

        if (notesField.contains("planned")) {
            return Transaction.TransType.PLANNED;
        }

        return Transaction.TransType.FUTURE;
    }
}
