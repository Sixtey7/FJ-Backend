package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.model.transport.TxUpdate;
import com.sixtey7.fjservice.utils.AccountHelper;
import com.sixtey7.fjservice.utils.TransHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

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
     * Clears the database and then parses and stores in the database all of
     * the {@link Transaction} and {@link Account} from the provided
     * {@link String} from a CSV File
     * @param textFromCSV {@link String} containing the input from a CSV File
     * @return {@link TxUpdate} containing the items that have been stored in the database
     */
    public TxUpdate parseAndClearAndStoreAllFromCSV(String textFromCSV) {
        //Delete everything from the database
        txDao.deleteAllTransactions();
        acctDao.deleteAllAccounts();

        //Store everything in the database
        TxUpdate updateFromCSV = parseAndStoreAllFromCSV(textFromCSV);

        return updateFromCSV;
    }


    /**
     * Parses and stores in the database all of the {@link Transaction} and
     * {@link Account} from the provided {@link String} from CSV File
     * @param textFromCSV Input from a CSV File
     * @return {@link TxUpdate} of the items that have been stored in the database
     */
    public TxUpdate parseAndStoreAllFromCSV(String textFromCSV) {
        TxUpdate updatesFromCSV = parseAllFromCSV(textFromCSV);

        //store everything in the database
        acctDao.addAllAccounts(updatesFromCSV.getAccounts());
        txDao.addAllTransactions(updatesFromCSV.getTransactions());

        return updatesFromCSV;
    }

    /**
     * Clears the {@link Account} database and then parses and stores in the database all of the
     * accounts in the provided CSV
     * @param textFromCSV Input from the CSV File
     * @return {@link List} of the parsed {@link Account}
     */
    public List<Account> parseAndClearAndStoreAccountFromCSV(String textFromCSV) {
        acctDao.deleteAllAccounts();

        List<Account> updateFromCSV = parseAndStoreAccountFromCSV(textFromCSV);

        return updateFromCSV;
    }

    /**
     * Parses and stores in the database all of the {@link Account} from the CSV File
     * @param textFromCSV Input from the CSV File
     * @return {@link List} of the parsed {@link Account}
     */
    public List<Account> parseAndStoreAccountFromCSV(String textFromCSV) {
        List<Account> updatesFromCSV = parseAccounts(textFromCSV);

        acctDao.addAllAccounts(updatesFromCSV);

        return updatesFromCSV;
    }

    /**
     * Clears the {@link Transaction} database and then parses and stores in the data all of
     * transactions in the provided CSV
     * @param textFromCSV Input from the CSV File
     * @param acctMap {@link Map} of {@link Account} name ({@link String} to Account {@link UUID}
     * @return {@link List} of parsed {@link Transaction}
     */
    public List<Transaction> parseAndClearAndStoreTxFromCSV(String textFromCSV, Map<String, UUID> acctMap) {
        txDao.deleteAllTransactions();

        List<Transaction> updateFromCSV = parseAndStoreTxFromCSV(textFromCSV, acctMap);

        return updateFromCSV;
    }

    /**
     * Parses and stores in the database all of the {@link Transaction} from the CSV File
     * @param textFromCSV Input from the CSV File
     * @param acctMap {@link Map} of {@link Account} name ({@link String} to Account {@link UUID}
     * @return {@link List} of parsed {@link Transaction}
     */
    public List<Transaction> parseAndStoreTxFromCSV(String textFromCSV, Map<String, UUID> acctMap) {
        List<Transaction> updatesFromCSV = parseTransactions(textFromCSV, acctMap);

        txDao.addAllTransactions(updatesFromCSV);

        return updatesFromCSV;
    }

    /**
     * Parses all of the {@link Transaction} and {@link Account}
     * from the provided {@link String} from CSV File
     * @param textFromCSV Input from the CSV File
     * @return {@link TxUpdate} object containing all of the parsed items
     */
    public TxUpdate parseAllFromCSV(String textFromCSV) {
        TxUpdate returnValue = new TxUpdate();

        String sections[] = textFromCSV.split("~!~");
        String accounts;
        String transactions;

        if (sections.length == 2) {
            accounts = sections[0];
            transactions = sections[1];
        }
        else if (sections.length == 3) {
            // For some reason, the split tends to return 3, with the first being empty, handle this case
            if (sections[0].equals("")) {
                accounts = sections[1];
                transactions = sections[2];
            }
            else {
                LOGGER.error("Incorrect number of sections provided, expected 2 got {}", sections.length);
                throw new IllegalArgumentException("Incorrect number of sections provided, expected 2 got " + sections.length);
            }
        }
        else {
            LOGGER.error("Incorrect number of sections provided, expected 2 got {}", sections.length);
            throw new IllegalArgumentException("Incorrect number of sections provided, expected 2 got " + sections.length);
        }

        /* Accounts */
        //need to strip off the first line
        int firstSlashNPos = accounts.indexOf("\\n");
        String accountSection = accounts.substring(firstSlashNPos + 1);
        LOGGER.debug("Captured account section\n{}", accountSection);

        returnValue.getAccounts().addAll(parseAccounts(accountSection));

        //build the accounts map for transactions
        Map<String, UUID> accountNameMap = acctHelper.buildNameToUUIDMap(returnValue.getAccounts());

        /* Transactions */
        //need to strip off the first line
        int firstTxSlashNPos = transactions.indexOf("\\n");
        String transSection = transactions.substring(firstTxSlashNPos + 1);
        LOGGER.debug("Captured Transaction section\n{}", transSection);

        returnValue.getTransactions().addAll(parseTransactions(transSection, accountNameMap));

        returnValue.setSuccess(true);
        
        return returnValue;
    }

    /**
     * Parses all of the accounts from the provided CSV Text
     * @param textFromCSV {@link String} containing rows of CSV data
     * @return {@link List} of {@link Account} parsed from the provided data
     */
    public List<Account> parseAccounts(String textFromCSV) {
        List<Account> returnList = new ArrayList<>();

        String[] allLines = textFromCSV.split("\\n");
        LOGGER.info("Found {} Accounts!", allLines.length);

        for (int lineCounter = 1; lineCounter < allLines.length; lineCounter++) {
            returnList.add(generateAccountFromString(allLines[lineCounter]));
        }

        return returnList;
    }

    /**
     * Parses all of the transactions from the provided CSV Text
     * @param textFromCSV {@link String} from the CSV File
     * @param accountNameMap {@link Map} of account name {@link String} to account UUID {@link UUID}
     * @return
     */
    public List<Transaction> parseTransactions(String textFromCSV, Map<String, UUID> accountNameMap) {
        List<Transaction> returnList = new ArrayList<>();

        String[] allLines = textFromCSV.split("\\n");
        LOGGER.info("Found {} Transactions!", allLines.length);

        for (int lineCounter = 1; lineCounter < allLines.length; lineCounter++) {
            returnList.add(generateTxFromString(allLines[lineCounter], accountNameMap));
        }

        return returnList;
    }

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

        if (lineData.length != 5) {
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
     * @param accountNameMap {@link Map} of account name {@link String} to account UUID {@link UUID}
     * @return {@link Transaction} created from the String
     */
    private Transaction generateTxFromString(String csvLine, Map<String, UUID> accountNameMap) {
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

        String accountName = lineData[3];
        UUID accountUUID = accountNameMap.get(accountName);

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

