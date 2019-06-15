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
     * Used to massage transaction data
     */
    @Inject
    private AccountHelper acctHelper;

    /**
     * Used to get transaction data
     */
    @Inject
    private TransactionDAO txDao;


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
        StringBuilder csvFile = new StringBuilder();
        for (Transaction tx : allTxs) {
            if (tx.getAmount() != null) {
                csvFile.append(generateCSVLineFromTransaction(tx, acctNameMap.get(tx.getAccountId().toString())));
            }
            else {
                csvFile.append(generateCSVLineFromTransaction(tx, ""));
            }
        }

        return csvFile.toString();
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

        sb.append(tx.getDateAsLocalDT());
        sb.append(",");

        sb.append(tx.getNotes());

        sb.append("\n");

        return sb;
    }
}
