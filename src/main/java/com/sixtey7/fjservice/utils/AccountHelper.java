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
import java.util.*;

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
     * @param accountUUID a {@link UUID} capturing the id of the account to update
     * @return boolean indicating if the balance was successfully updated
     */
    public Account updateBalanceForAccount(UUID accountUUID) {
        return updateBalanceForAccount(accountUUID.toString());
    }

    /**
     * Kicks off the calculation of the balance for the specified account
     * @param accountId a {@link String} capturing the UUID of the account to update
     * @return boolean indicating if the balance was successfully updated
     */
    public Account updateBalanceForAccount(String accountId) {
        LOGGER.info("Updating balance for account with id {}", accountId);
        Account accountToUpdate = accountDAO.getAccount(accountId);

        if (accountToUpdate == null) {
            LOGGER.error("Failed to find account with id: {}", accountId);
            return null;
        }

        if (!accountToUpdate.getDynamic()) {

            accountToUpdate = updateBalanceForAccount(accountToUpdate);

            if (!accountDAO.updateAccount(accountToUpdate)) {
                return null;
            }
        }
        else {

            LOGGER.warn("Update balance for account was called for account {} but the account was not dynamic!", accountId);
        }

        return accountToUpdate;
    }

    /**
     * Kicks off the calculation of the balance for the provided account
     * @param accountToUpdate {@link Account} to be updated
     * @return {@link Account} that has had its balance updated
     */
    public Account updateBalanceForAccount(Account accountToUpdate) {
        if (!accountToUpdate.getDynamic()) {
            List<Transaction> txList = transDAO.getTxForAccount(accountToUpdate.getId().toString());

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

        float balance = 0;
        for (Transaction thisTrans : txList) {
            if (thisTrans.getType().equals(Transaction.TransType.CONFIRMED)) {
                LOGGER.debug("Balance before {}", balance);
                LOGGER.debug("Amount {}", thisTrans.getAmount());
                balance += thisTrans.getAmount();
                LOGGER.debug("Balance after {}", balance);
            }
            //NOTE: This used to break once it hit one that wasn't CONFIRMED, but its possible to have several
            // entries on the same day, so we can't rely on the first bad one to be the last entry
            //TODO: Fix the above
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

    /**
     * Builds a map of account names in String form to Account id
     * @param accountsToMap {@link List} of {@link Account} objects to build the map from
     * @return {@link Map} of {@link String} to {@link UUID} for account name to id
     */
    public Map<String, UUID> buildNameToUUIDMap(List<Account> accountsToMap) {
        Map<String, UUID> returnMap = new HashMap<>();

        for (Account acct : accountsToMap) {
            returnMap.put(acct.getName(), acct.getId());
        }

        return returnMap;
    }
}
