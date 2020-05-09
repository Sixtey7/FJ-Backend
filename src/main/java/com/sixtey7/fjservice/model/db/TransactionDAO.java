package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO class for the Transactions table
 */
@Dependent
public class TransactionDAO {

    /**
     * Entity manager to be used for this DAO
     */
    @Inject
    private EntityManager em;

    /**
     * Logger to be used for this class
     */
    private static final Logger LOGGER = LogManager.getLogger(TransactionDAO.class);

    /**
     * Returns all of the transactions in the database
     * @return {@link List} of {@link Transaction}
     */
    public List<Transaction> getAllTransactions() {
        LOGGER.debug("Getting all transactions!");

        List<Transaction> returnList = em.createQuery("Select t from Transaction t", Transaction.class).getResultList();

        LOGGER.debug("Returning {} transactions", returnList.size());

        return returnList;
    }

    /**
     * Returns the data for the specified {@link Transaction}
     * @param transId String containing the UUID of the {@link Transaction}
     * @return {@link Transaction} specified by the id or null if not found
     */
    public Transaction getTransaction(String transId) {
        LOGGER.debug("Getting the transaction for id {}", transId);
        try {
            return em.createQuery("Select t from Transaction t where t.id = :transId", Transaction.class)
                    .setParameter("transId", transId)
                    .getSingleResult();
        }
        catch(NoResultException nre) {
            LOGGER.warn("Failed to find transaction with id {}", transId);
            return null;
        }
    }

    /**
     * Returns all of the transactions in the database between the two provided values
     * @param startDate {@link LocalDate} containing the first date of the interval
     * @param endDate {@link LocalDate} containing the second date of the interval
     * @return {@link List} of {@link Transaction} that are between the specified dates
     */
    public List<Transaction> getTxBetweenDates(LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Getting transactions between {} and {}", startDate, endDate);

        List<Transaction> returnList =  em.createQuery("Select t from Transaction t where t.date BETWEEN :strDate AND :endDate", Transaction.class)
                .setParameter("strDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        LOGGER.debug("Returning {} transactions!", returnList.size());

        return returnList;

    }

    /**
     * Returns all of the transactions newer than the provided date
     * @param startDate {@link LocalDate} to start the filter from
     * @return {@link List} of {@link Transaction} that are newer than the provided date
     */
    public List<Transaction> getTxNewerThan(LocalDate startDate) {
        LOGGER.debug("Getting transactions newer than {}", startDate);

        List<Transaction> returnList = em.createQuery("Select t from Transaction t where t.date > :strDate", Transaction.class)
                    .setParameter("strDate", startDate)
                .getResultList();

        LOGGER.debug("Returning {} transactions", returnList.size());

        return returnList;
    }

    /**
     * Returns all of the {@link Transaction} mapped to the provided account id
     * @param accountId String containing the UUID of the account
     * @return {@link List} of {@link Transaction} matching the provided account UUID
     */
    public List<Transaction> getTxForAccount(final String accountId) {
        LOGGER.debug("Getting all transaction for account {}", accountId);

        List<Transaction> returnTxs = em.createQuery("Select t from Transaction t where t.accountId = :acctId" , Transaction.class)
                .setParameter("acctId", accountId)
                .getResultList();

        LOGGER.debug("Returning {} transactions", returnTxs);
        return returnTxs;
    }

    /**
     * Adds the provided {@link Transaction} to the database
     * @param txToAdd {@link Transaction} to be persisted
     * @return String containing the UUID of the {@link Transaction} that was persisted
     */
    @Transactional
    public String addTransaction(Transaction txToAdd) {
        LOGGER.debug("Adding a new transaction!");
        if (txToAdd.getId() == null) {
            UUID id = UUID.randomUUID();

            LOGGER.debug("Generated the id {}", id);
            txToAdd.setId(id);
        }

        try {
            em.persist(txToAdd);
        }
        catch(Exception ex) {
            LOGGER.error("Failed to persist transaction", ex);
        }

        return txToAdd.getId().toString();
    }

    /**
     * Adds all of the provided transactions
     * @param txsToPersist {@link List} of {@link Transaction} that are to be persisted
     * @return {@link List} of Strings containing the UUIDs that were persisted
     */
    public List<String> addAllTransactions(List<Transaction> txsToPersist) {
        LOGGER.debug("Saving {} transactions", txsToPersist.size());

        List<String> returnList = new ArrayList<>(txsToPersist.size());

        txsToPersist.forEach(tx -> {
            // This is technically risky as null could be returned above, but that'll never happen
                // Famous last words...
            returnList.add(this.addTransaction(tx));
        });

        LOGGER.debug("Added {} transactions", returnList.size());
        return returnList;
    }

    /**
     * Updates the provided transaction in the database
     * @param txToUpdate {@link Transaction} to be updated
     * @return Boolean on whether or not the save was successful
     */
    @Transactional
    public boolean updateTransaction(Transaction txToUpdate) {
        LOGGER.debug("Updating transaction {}", txToUpdate.getId().toString());

        try {
            em.merge(txToUpdate);
        }
        catch(Exception ex) {
            LOGGER.error("Failed to persist update of transaction", ex);
            return false;
        }

        return true;
    }

    /**
     * Deletes the {@link Transaction} associated with the provided id
     * @param idToDelete String containing the id of the {@link Transaction} to be deleted
     * @return integer capturing the number of records deleted (nominally one)
     */
    @Transactional
    public int deleteTransaction(String idToDelete) {
        LOGGER.debug("Deleting transaction {}", idToDelete);

        int returnValue = em.createQuery("Delete from Transaction t where t.id = :transId")
                .setParameter("transId", idToDelete)
                .executeUpdate();

        LOGGER.debug("Deleted {} transactions", returnValue);

        return returnValue;
    }

    /**
     * Deletes all of the {@link Transaction} in the database
     * @return the number of entries deleted
     */
    @Transactional
    public int deleteAllTransactions() {
        LOGGER.debug("Deleting all transactions!");

        int returnValue = em.createQuery("Delete from Transaction t").executeUpdate();

        LOGGER.debug("Deleted {} transactions", returnValue);

        return returnValue;
    }
}
