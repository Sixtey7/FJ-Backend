package com.sixtey7.fjservice.model.converter;

import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.utils.AccountHelper;
import com.sixtey7.fjservice.utils.TransHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

}

