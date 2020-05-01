package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.utils.AccountHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Class used to generate a CSV File based on the transaction data
 */
@Dependent
public class CSVGenerator {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER =LogManager.getLogger(CSVGenerator.class);

    /**
     * Used to get the account data
     */
    @Inject
    private AccountDAO acctDao;

    /**
     * Used to massage account data
     */
    @Inject
    private AccountHelper acctHelper;

    /**
     * Used to get transaction data
     */
    @Inject
    private TransactionDAO txDao;


    public String generateStringForAllData() {
        List<Account> allAccounts = acctDao.getAllAccounts();
        List<Transaction> allTxs = txDao.getAllTransactions();

        Map<String, String> acctNameMap = acctHelper.buildIdNameMap(allAccounts);

        StringBuilder csvFile = new StringBuilder();

        // All Accounts
        csvFile.append("~!~,Accounts");
        csvFile.append("\n");
        csvFile.append(buildStringBuilderForAccounts(allAccounts));

        // All Txs
        csvFile.append("~!~,Transactions");
        csvFile.append("\n");
        csvFile.append(buildStringBuilderForAllTxs(allTxs, acctNameMap));

        return csvFile.toString();

    }
    /**
     *
     * Returns a string that captures all of the account data in CSV Format
     * @return {@link String} holding all {@link Account} data
     */
    public String generateCSVForAllAccounts() {
        LOGGER.info("Generating csv for all accounts");
        List<Account> allAccounts = acctDao.getAllAccounts();


        return buildStringBuilderForAccounts(allAccounts).toString();
    }

    /**
     * Returns a string that captures all of the data in CSV Format
     * @return {@link String} holding all {@link Transaction} data
     */
    public String generateCSVForAllTxs() {
        LOGGER.info("Generating csv for all transactions");
        List<Transaction> allTxs = txDao.getAllTransactions();

        //Get all of the accounts for reference
        List<Account> allAccounts = acctDao.getAllAccounts();
        Map<String, String> acctNameMap = acctHelper.buildIdNameMap(allAccounts);

        LOGGER.debug("found {} transactions", allTxs.size());

        return buildStringBuilderForAllTxs(allTxs, acctNameMap).toString();

    }

    private StringBuilder buildStringBuilderForAccounts(List<Account> allAccounts) {
        StringBuilder csvFile = new StringBuilder();
        for (Account acct : allAccounts) {
            csvFile.append(generateCSVLineFromAccount(acct));
        }

        return csvFile;
    }

    private StringBuilder buildStringBuilderForAllTxs(List<Transaction> allTxs, Map<String, String> acctNameMap) {
        StringBuilder csvFile = new StringBuilder();
        for (Transaction tx : allTxs) {
            if (acctNameMap.get(tx.getAccountId().toString()) != null) {
                csvFile.append(generateCSVLineFromTransaction(tx, acctNameMap.get(tx.getAccountId().toString())));
            }
            else {
                csvFile.append(generateCSVLineFromTransaction(tx, ""));
            }
        }

        return csvFile;

    }

    /**
     * Builds a {@link StringBuilder} for a single {@link Transaction}
     * @param tx {@link Transaction} to build the line for
     * @param acctName {@link String} name of the parent account
     * @return {@link StringBuilder} containing the data
     */
    private StringBuilder generateCSVLineFromTransaction(Transaction tx, String acctName) {
        LOGGER.debug("Building a line for {}", tx.getId());
        StringBuilder sb = new StringBuilder();

        sb.append(tx.getName());
        sb.append(",");

        if (tx.getAmount() < 0) {
            sb.append((tx.getAmount() * -1));
            sb.append(",");
        }
        else {
            sb.append(",");
            sb.append(tx.getAmount());
        }
        sb.append(",");

        sb.append(acctName);
        sb.append(",");

        sb.append(tx);
        sb.append(",");

        sb.append(tx.getType().toString());
        sb.append(",");

        sb.append(tx.getNotes());

        sb.append("\n");

        return sb;
    }

    /**
     * Builds a {@link StringBuilder} for a single {@link Account}
     * @param acct {@link Account} to build the line for
     * @return {@link StringBuilder} containing the data
     */
    private StringBuilder generateCSVLineFromAccount(Account acct) {
        LOGGER.debug("Building a line for {}", acct.getId());
        StringBuilder sb = new StringBuilder();

        sb.append(acct.getName());
        sb.append(",");

        if (acct.getAmount() < 0) {
            sb.append((acct.getAmount() * -1));
            sb.append(",");
        }
        else {
            sb.append(",");
            sb.append(acct.getAmount());
        }
        sb.append(",");

        sb.append(acct.getNotes());
        sb.append(",");

        if (acct.getDynamic()) {
            sb.append("Dynamic");
        }
        else {
            sb.append("Calculated");
        }
        sb.append(",");

        sb.append("\n");


        return sb;


    }
}
