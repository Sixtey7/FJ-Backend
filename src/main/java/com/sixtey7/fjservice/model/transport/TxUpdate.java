package com.sixtey7.fjservice.model.transport;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to capture the interface upon transaction updates
 */
public class TxUpdate {
    /**
     * List of transactions that have been updated
     */
    private List<Transaction> transactions;

    /**
     * List of impacted accounts
     */
    private List<Account> accounts;

    /**
     * holds whether or not the update was a success;
     */
    private boolean success;

    /**
     * Default constructor
     */
    public TxUpdate() {
        this.transactions = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
