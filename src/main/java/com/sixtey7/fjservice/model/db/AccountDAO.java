package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Account;
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
     * @return {@link List} containing all of the {@link Account} entries in the database
     */
    public List<Account> getAllAccounts() {
        LOGGER.debug("Getting all accounts");

        List<Account> returnList = em.createQuery("Select a from Account a", Account.class).getResultList();

        LOGGER.debug("Returning {} accounts", returnList.size());
        return returnList;
    }

    /**
     * Returns the data for the specified account
     * @param accountId String representing the UUID of the account to get
     * @return {@link Account} object that was requested (null if not found)
     */
    public Account getAccount(String accountId) {
        LOGGER.debug("Getting account for id: {}", accountId);
        try {
            Account acct = (Account) em.createQuery("Select a from Account a where a.id = :acctId")
                    .setParameter("acctId", accountId)
                    .getSingleResult();

            return acct;
        }
        catch(NoResultException nre) {
            // TODO: Probably shouldn't rely on catching an error here and handle this more gracefully
            LOGGER.warn("Failed to find account with id: {}", accountId);
            return null;
        }
    }

    /**
     * Adds the provided account to the database
     * @param acctToAdd {@link Account} the account object to be persisted
     * @return String containing the id of the added account
     */
    @Transactional
    public String addAccount(Account acctToAdd) {
        LOGGER.debug("Adding a new account!");

        if (acctToAdd.getId() == null) {
            acctToAdd.setId(UUID.randomUUID());
        }

        try {
            em.persist(acctToAdd);
        }
        catch (Exception ex) {
            LOGGER.error("Failed to persist account", ex);
            return null;
        }

        return acctToAdd.getId().toString();
    }

    /**
     * Adds all of the provided accounts
     * @param acctsToPersist {@link List} of {@link Account} that are to be persisted
     * @return {@link List} of Strings containing the UUIDs that were persisted
     */
    public List<String> addAllAccounts(List<Account> acctsToPersist) {
        LOGGER.debug("Saving {} accounts", acctsToPersist.size());

        List<String> returnList = new ArrayList<>(acctsToPersist.size());

        acctsToPersist.forEach(acct -> {
            returnList.add(this.addAccount(acct));
        });

        LOGGER.debug("Added {} accounts", returnList.size());
        return returnList;
    }

    /**
     * Updates the provided {@link Account}
     * @param accountToUpdate {@link Account} object to save
     * @return boolean on whether or not the save was successful
     */
    @Transactional
    public boolean updateAccount(Account accountToUpdate) {
        LOGGER.debug("Updating account {}", accountToUpdate.getId());

        try {
            em.merge(accountToUpdate);
        }
        catch (Exception ex) {
            LOGGER.error("Failed tio persist update of account", ex);
            return false;
        }

        return true;
    }

    /**
     * Deletes the account matching the provided UUID
     * @param idToDelete String containing the UUID of the account to delete
     * @return integer capturing the number of records deleted (nominally 1)
     */
    @Transactional
    public int deleteAccount(String idToDelete) {
        LOGGER.debug("Deleting account {}", idToDelete);
        int returnVal = em.createQuery("Delete from Account a where a.id = :acctId")
                .setParameter("acctId", idToDelete)
                .executeUpdate();

        LOGGER.debug("Deleted {} accounts", returnVal);

        return returnVal;
    }

    @Transactional
    public int deleteAllAccounts() {
        LOGGER.debug("Deleting all accounts!");

        int returnVal = em.createQuery("Delete from Account a").executeUpdate();

        LOGGER.debug("Deleted {} accounts", returnVal);

        return returnVal;
    }
}
