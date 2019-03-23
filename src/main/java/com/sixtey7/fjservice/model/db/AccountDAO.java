package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.AccountRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO Class for the Accounts table
 */
@Dependent
public class AccountDAO {

    /**
     * Entity Manager to be used for this DAO
     */
    @Inject
    EntityManager em;

    /**
     * LOGGER to be used for this class
     */
    private static final Logger LOGGER = LogManager.getLogger(AccountDAO.class);

    /**
     * Returns all of the accounts in the database
     * @return {@link List} containing all of the {@link Account} in the database
     */
    public List<Account> getAllAccounts() {
        LOGGER.debug("Getting all accounts");
        List<AccountRecord> records = em.createQuery("Select a from AccountRecord a", AccountRecord.class).getResultList();

        List<Account> returnList = new ArrayList<>();

        records.forEach(accountRecord -> returnList.add(accountRecord.getData()));

        LOGGER.debug("Returning {} Accounts", returnList.size());
        return returnList;
    }

    /**
     * Returns the data for the specified account
     * @param accountId String representing the UUID of the account to get data for
     * @return {@link Account} object that was requested (null if not present)
     */
    public Account getAccount(String accountId) {
        LOGGER.debug("Getting account for id {}", accountId);
        try {
            AccountRecord ar = (AccountRecord) em.createQuery("Select a from AccountRecord a where a.id = '" + accountId + "'").getSingleResult();

            return ar.getData();
        }
        catch (NoResultException nre) {
            //TODO: Probably shouldn't rely on catching an error here and handle this smoother
            LOGGER.warn("Failed to find account with id: {}", accountId);
            return null;
        }
    }

    /**
     * Adds the provided account to the database
     * @param accountToAdd {@link Account} the account object to add to the database
     * @return String containing the UUID of the added account
     */
    @Transactional
    public String addAccount(Account accountToAdd) {
        LOGGER.debug("Adding a new account!");

        UUID newId = UUID.randomUUID();
        LOGGER.debug("Generated the id {}", newId);
        accountToAdd.setId(newId);

        AccountRecord arToPersist = new AccountRecord(newId, accountToAdd);

        try {
            em.persist(arToPersist);
        }
        catch (Exception ex) {
            LOGGER.error("Failed to persist account", ex);
            return null;
        }

        return newId.toString();
    }

    /**
     * Updates the account matching the provided account id with the provided {@link Account}
     * @param accountId String containing the UUID of the account
     * @param accountToUpdate {@link Account} the account object to save
     * @return boolean on whether or not the save was successful
     */
    @Transactional
    public boolean updateAccount(String accountId, Account accountToUpdate) {
        LOGGER.debug("Updating account {}", accountId);
        AccountRecord accountToPersist = new AccountRecord(UUID.fromString(accountId), accountToUpdate);

        try {
            em.merge(accountToPersist);
        }
        catch(Exception ex) {
            LOGGER.error("Failed to persist update of account", ex);
            return false;
        }

        return true;
    }

    /**
     * Deletes the account matching the provided UUID
     * @param idToDelete String containing the UUID of the account to delete
     * @return integer capturing the number of records deleted (nominally 0 or 1)
     */
    @Transactional
    public int deleteAccount(String idToDelete) {
        LOGGER.debug("Deleting account {}", idToDelete);
        int returnVal = em.createQuery("Delete from AccountRecord a where a.id = '" + idToDelete + "';").executeUpdate();

        LOGGER.debug("Deleting {} accounts", returnVal);
        return returnVal;
    }

    /**
     * Deletes all of the accounts in the database
     * @return integer capturing the number of records deleted
     */
    @Transactional
    public int deleteAllAccounts() {
        LOGGER.debug("Deleting all accounts!");

        int returnVal = em.createQuery("Delete from AccountRecord a").executeUpdate();

        LOGGER.debug("Deleting {} accounts", returnVal);
        return returnVal;
    }



}
