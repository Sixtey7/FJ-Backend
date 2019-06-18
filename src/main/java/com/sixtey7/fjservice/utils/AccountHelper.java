package com.sixtey7.fjservice.utils;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.db.AccountDAO;
import com.sixtey7.fjservice.model.db.TransactionDAO;
import com.sixtey7.fjservice.rest.AccountResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for dealing with {@link Account objects}
 */
@Dependent
public class AccountHelper {


    //Create a logger for the class
    private static final Logger LOGGER = LogManager.getLogger(AccountResource.class);


    /**
     * DAO used for handling {@link Transaction} objects
     */
    @Inject
    TransactionDAO transDAO;

    /**
     * DAO for {@link Account} objects
     */
    @Inject
    AccountDAO accountDAO;

    /**
     * Kicks off the calculation of the balance for the specified account
     * @param accountId a {@link String} capturing the UUID of the account to update
     * @return boolean indicating if the balance was successfully updated
     */
    public boolean updateBalanceForAccount(String accountId) {
        LOGGER.info("Updating balance for account with id {}", accountId);
        Account accountToUpdate = accountDAO.getAccount(accountId);

        if (accountToUpdate == null) {
            LOGGER.error("Failed to find account with id: {}", accountId);
            return false;
        }

        if (!accountToUpdate.getDynamic()) {

            accountToUpdate = updateBalanceForAccount(accountToUpdate);

            return accountDAO.updateAccount(accountId, accountToUpdate);
        }

        // return true - nothing was updated, but nothing should have been
        return true;
    }

    /**
     * Kicks off the calculation of the balance for the provided account
     * @param accountToUpdate {@link Account} to be updated
     * @return {@link Account} that has had its balance updated
     */
    public Account updateBalanceForAccount(Account accountToUpdate) {
        if (!accountToUpdate.getDynamic()) {
            List<Transaction> txList = transDAO.getTransForAccount(accountToUpdate.getId().toString());

            LOGGER.info("Got {} transactions for account {}", txList.size(), accountToUpdate.getId().toString());
            return updateBalanceForAccount(accountToUpdate, txList);
        }

        return accountToUpdate;
    }

    /**
     * Method that does the actual calculation of the accounts balance
     * @param accountToUpdate {@link Account} to have its balance updated
     * @param txList {@link List} of {@link Transaction} pertaining to the account
     * @return {@link Account} that has the balance updated
     */
    private Account updateBalanceForAccount(Account accountToUpdate, List<Transaction> txList) {

        //first, sort the list of transactions
        Collections.sort(txList);

        LocalDate now = LocalDate.now();

        float balance = 0;
        for (Transaction thisTrans : txList) {
            if (!(thisTrans.getDateAsLocalDT().isAfter(now))) {
                LOGGER.debug("Balance before {}", balance);
                LOGGER.debug("Amount {}", thisTrans.getAmount());
                balance += thisTrans.getAmount();
                LOGGER.debug("Balance after {}", balance);
            }
            else {
                LOGGER.debug("Breaking!");
                break;
            }
        }

        LOGGER.info("Final Balance: {}", balance);
        accountToUpdate.setAmount(balance);

        return accountToUpdate;

    }

    /**
     * Builds a map of Account Ids (in String form) to Account Name
     * @param accountsToMap {@link List} of {@link Account} objects to build the map from
     * @return {@link Map} of {@link String} to {@link String} for account id to name
     */
    public Map<String, String> buildIdNameMap(List<Account> accountsToMap) {
        Map<String, String> returnMap = new HashMap<>();

        for (Account acct : accountsToMap) {
            returnMap.put(acct.getId().toString(), acct.getName());
        }

        return returnMap;

    }
}
