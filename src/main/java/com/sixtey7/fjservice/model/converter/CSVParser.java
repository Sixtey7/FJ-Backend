package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.utils.AccountHelper;
import com.sixtey7.fjservice.utils.TransHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
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
     * Generates a single transaction from a line from the CSV File
     * @param csvLine the line from the file
     * @param accountUUID {@link UUID} of the account
     * @return {@link Transaction} created from the String
     */
    private Transaction generateTxFromString(String csvLine, UUID accountUUID) {
        return new Transaction();
    }

}

