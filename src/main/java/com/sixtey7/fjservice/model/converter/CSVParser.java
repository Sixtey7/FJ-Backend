package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.utils.AccountHelper;
import com.sixtey7.fjservice.utils.TransHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Class used to parse a generated CSV File
 */
@Dependent
public class CSVParser {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LogManager.getLogger(CSVParser.class);

    /**
     * Used to interact with account data
     */
    @Inject
    private AccountDAO acctDao;

    /**
     * Used to massage account data
     */
    @Inject
    private AccountHelper acctHelper;

    /**
     * Used to interact with transaction data
     */
    @Inject
    private TransactionDAO txDao;

    /**
     * Used to massage tx data
     */
    @Inject
    private TransHelper txHelper;

    /**
     * Generates a single account from a line from the CSV File
     * @param csvLine {@link String} the line from the file
     * @return {@link Account} generated from the parsed line
     */
    private Account generateAccountFromString(String csvLine) {
        /* Expected Order
        0 - Name
        1 - Debit
        2 - Credit
        3 - Notes
        4 - Type (Dynamic / Calculated)
         */

        String[] lineData = csvLine.split(",", 5);

        if (lineData.length != 7) {
            throw new IllegalArgumentException("Incorrect number of entries provided, expected 5 got " + lineData.length);
        }

        String name = lineData[0];

        float amount = determineAmount(lineData[1], lineData[2]);

        String notes = lineData[3];

        boolean dynamic = true;
        //TODO: This string should be a constant somewhere
        if (lineData[4].equals("Calculated")) {
            dynamic = false;
        }

        Account newAccount = new Account(name, amount, notes, dynamic);

        LOGGER.debug("    ~~~~~");
        LOGGER.debug(newAccount.toString());
        LOGGER.debug("    ~~~~~");

        return newAccount;

    }
    /**
     * Generates a single transaction from a line from the CSV File
     * @param csvLine the line from the file
     * @param accountUUID {@link UUID} of the account
     * @return {@link Transaction} created from the String
     */
    private Transaction generateTxFromString(String csvLine, UUID accountUUID) {
        /* Expected order
        0 - Name
        1 - Debit
        2 - Credit
        3 - Account Name
        4 - Date
        5 - Type
        6 - Notes
         */

        String[] lineData = csvLine.split(",", 7);

        if (lineData.length != 7) {
            throw new IllegalArgumentException("Incorrect number of lines provided, expected 7 got " + lineData.length);
        }

        String name = lineData[0];

        float amount = determineAmount(lineData[1], lineData[2]);

        LocalDate transDate = LocalDate.now();

        if (!lineData[4].equals("")) {
            LOGGER.debug("Got the time: {}", lineData[4]);
            transDate = LocalDate.parse(lineData[4]);
        }

        Transaction.TransType type = Transaction.TransType.FUTURE;
        if (!lineData[5].equals("")) {
            type = Transaction.TransType.valueOf(lineData[5]);
        }

        String notes = lineData[6];

        Transaction newTrans = new Transaction(name, transDate, amount, accountUUID, notes, type);

        LOGGER.debug("    ~~~~~");
        LOGGER.debug(newTrans.toString());
        LOGGER.debug("    ~~~~~");

        return newTrans;
    }

    /**
     * Parses an amount value out of the provided debit and credit strings
     * @param debit {@link String} containing the debit value
     * @param credit {@link String} containing the credit value
     * @return float containing the parsed value (or 0 if no value could be parsed)
     */
    private float determineAmount(String debit, String credit) {
        float amount = 0;

        if (!debit.equals("")) {
            amount = -1 * getAmountFromString(debit);
        }
        else if (!credit.equals("")) {
            amount = getAmountFromString(credit);
        }
        else {
            LOGGER.warn("Failed to parse an amount Debit: {} Credit {}", debit, credit);
        }

        return amount;
    }

    /**
     * Parses a float out of the monetary string (handles the $)
     * @param amountString a {@link String} containing the value
     * @return float parsed from the string
     */
    private float getAmountFromString(String amountString) {
        String value = amountString;
        if (value.charAt(0) == '$') {
            value = value.substring(1);
        }

        return Float.parseFloat(value);
    }

}

