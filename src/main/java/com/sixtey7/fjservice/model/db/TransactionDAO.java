package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.TransactionRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO Class for the Transaction table
 */
@Dependent
public class TransactionDAO {

    /**
     * EMF used to create the entity manager
     */
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");

    /**
     * Entity Manager to be used for this DAO
     */
    private EntityManager em = emf.createEntityManager();

    /**
     * LOGGER to be used for this class
     */
    private static final Logger LOGGER = LogManager.getLogger(TransactionDAO.class);

    /**
     * Returns all of the transactions in the database
     * @return {@link List} containing all of the {@link Transaction} in the database
     */
    public List<Transaction> getAllTransactions() {
        LOGGER.debug("Getting all transactions");

        List<TransactionRecord> records = em.createQuery("Select t from TransactionRecord t", TransactionRecord.class).getResultList();

        List<Transaction> returnList = new ArrayList<>();
        for (TransactionRecord tr : records) {
            LOGGER.trace("Adding transaction record with id {}", tr.getId());
            returnList.add(tr.getData());
        }

        LOGGER.debug("Returning {} transactions", returnList.size());

        return returnList;
    }

    /**
     * Returns the data for the specified Transaction
     * @param transactionId String representing the UUID of the transaction to get data for
     * @return {@link Transaction} object that was requested (null if not present)
     */
    public Transaction getTransaction(String transactionId) {
        LOGGER.debug("Getting transaction for id {}", transactionId);
        try {
            TransactionRecord tr = em.createQuery("Select t from TransactionRecord t where t.id = '" + transactionId + "'", TransactionRecord.class).getSingleResult();

            return tr.getData();
        }
        catch (NoResultException nre) {
            //TODO: Probably shouldn't rely on catching an error here and handle this smoother
            LOGGER.warn("Failed to find transaction with id: {}", transactionId);
            return null;
        }
    }

    /**
     * Gets all of the transactions that are tied to the specified record
     * @param accountId String containing the account id to search for
     * @return {@link List} of {@link Transaction} that match the specified account id
     */
    public List<Transaction> getTransForAccount(final String accountId) {
        LOGGER.debug("Getting all transactions for account {}", accountId);
        @SuppressWarnings("unchecked")
        List<TransactionRecord> records = em.createNativeQuery("Select * from Transactions t WHERE t.data->>'accountId' = '" + accountId + "'", TransactionRecord.class).getResultList();
        LOGGER.debug("Found {} transaction records", records.size());
        List<Transaction> returnList = new ArrayList<>();
        records.forEach(transactionRecord -> returnList.add(transactionRecord.getData()));

        LOGGER.trace("Build {} transactions to return", returnList.size());
        return returnList;
    }

    /**
     * Adds the provided transaction to the database
     * @param transactionToAdd {@link Transaction} to be added to the database
     * @return String containing the UUID of the added transaction
     */
    public String addTransaction(Transaction transactionToAdd) {
        LOGGER.debug("Adding a new transaction");
        UUID id = UUID.randomUUID();

        LOGGER.debug("Generated the id {}" , id);
        transactionToAdd.setTransId(id);

        TransactionRecord trToPersist = new TransactionRecord(id, transactionToAdd);

        try {
            em.getTransaction().begin();
            em.persist(trToPersist);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            LOGGER.error("Failed to persist transaction!", ex);
            return null;
        }

        return id.toString();
    }

    /**
     * Adds a list of transactions to the database
     * @param transList - {@link List} of {@link Transaction} to be added
     * @return {@link List} of Strings containing the UUIDs assigned to the transactions
     */
    public List<String> addAllTransactions(List<Transaction> transList) {
        LOGGER.debug("Saving {} transactions", transList.size());
        List<String> returnList = new ArrayList<>();

        //TODO: I'm sure there's a better way to do this - but not a faster one to code!
        for(Transaction thisTrans : transList) {
            String assignedId = this.addTransaction(thisTrans);
            LOGGER.trace("Adding {} to the array to return", assignedId);
            returnList.add(assignedId);
        }

        LOGGER.debug("Added {} transactions", returnList.size());
        return returnList;
    }

    /**
     * Updates the transaction matching the provided transaction id with the provided {@link Transaction}
     * @param transactionId String containing the UUID of the transaction
     * @param transToUpdate {@link Transaction} the transaction object to save
     * @return boolean on whether or not the save was successful
     */
    public boolean updateTransaction(String transactionId, Transaction transToUpdate) {
        LOGGER.debug("Updating transaction {}", transactionId);;
        TransactionRecord transactionToPersist = new TransactionRecord(UUID.fromString(transactionId), transToUpdate);

        try {
            em.getTransaction().begin();
            em.merge(transactionToPersist);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            LOGGER.error("Failed to persist update of transaction", ex);
            return false;
        }

        return true;
    }

    /**
     * Deletes the transaction matching the provided UUID
     * @param idToDelete String containing the UUID of the transaction to delete
     * @return integer capturing the number of records deleted (nominally 0 or 1)
     */
    public int deleteTransaction(String idToDelete) {
        LOGGER.debug("Deleting transaction {}", idToDelete);
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from TransactionRecord t where t.id = '" + idToDelete + ';').executeUpdate();
        em.getTransaction().commit();

        LOGGER.debug("Deleted {} transactions", returnVal);
        return returnVal;
    }

    /**
     * Deletes all of the transactions in the database
     * @return integer capturing the number of records deleted
     */
    public int deleteAllTransactions() {
        LOGGER.debug("Deleting all transactions");
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from TransactionRecord t").executeUpdate();
        em.getTransaction().commit();

        LOGGER.debug("Deleted {} transactions", returnVal);
        return returnVal;
    }
}
