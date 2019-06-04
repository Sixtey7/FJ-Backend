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
import java.util.List;

@Dependent
public class AccountHelper {


    //Create a logger for the class
    private static final Logger LOGGER = LogManager.getLogger(AccountResource.class);


    @Inject
    TransactionDAO transDAO;

    @Inject
    AccountDAO accountDAO;

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

    public Account updateBalanceForAccount(Account accountToUpdate) {
        if (!accountToUpdate.getDynamic()) {
            List<Transaction> txList = transDAO.getTransForAccount(accountToUpdate.getId().toString());

            LOGGER.info("Got {} transactions for account {}", txList.size(), accountToUpdate.getId().toString());
            return updateBalanceForAccount(accountToUpdate, txList);
        }

        return accountToUpdate;
    }

    private Account updateBalanceForAccount(Account accountToUpdate, List<Transaction> txList) {

        //first, sort the list of transactions
        Collections.sort(txList);

        LocalDate now = LocalDate.now();

        float balance = 0;
        for (Transaction thisTrans : txList) {
            if (!(thisTrans.getDateAsLocalDT().isAfter(now))) {
                LOGGER.debug("Balance before {}", balance);
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
}
