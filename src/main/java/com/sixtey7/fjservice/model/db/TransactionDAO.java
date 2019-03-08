package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Transaction;
import com.sixtey7.fjservice.model.TransactionRecord;

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
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");

    /**
     * Entity Manager to be used for this DAO
     */
    EntityManager em = emf.createEntityManager();

    /**
     * Returns all of the transactions in the database
     * @return {@link List} containing all of the {@link Transaction} in the database
     */
    public List<Transaction> getAllTransactions() {
        List<TransactionRecord> records = em.createQuery("Select t from TransactionRecord t", TransactionRecord.class).getResultList();

        List<Transaction> returnList = new ArrayList<>();
        for (TransactionRecord tr : records) {
            returnList.add(tr.getData());
        }

        return returnList;
    }

    /**
     * Returns the data for the specified Transaction
     * @param transactionId String representing the UUID of the transaction to get data for
     * @return {@link Transaction} object that was requested (null if not present)
     */
    public Transaction getTransaction(String transactionId) {
        try {
            TransactionRecord tr = em.createQuery("Select t from TransactionRecord t where t.id = '" + transactionId + "'", TransactionRecord.class).getSingleResult();

            return tr.getData();
        }
        catch (NoResultException nre) {
            //TODO: Probably shouldn't rely on catching an error here and handle this smoother
            return null;
        }
    }

    /**
     * Gets all of the transactions that are tied to the specified record
     * @param accountId String containing the account id to search for
     * @return {@link List} of {@link Transaction} that match the specified account id
     */
    public List<Transaction> getTransForAccount(final String accountId) {
        List<TransactionRecord> records = em.createNativeQuery("Select * from Transactions t WHERE t.data->>'accountId' = '" + accountId + "'", TransactionRecord.class).getResultList();
        List<Transaction> returnList = new ArrayList<>();
        records.forEach(transactionRecord -> returnList.add(transactionRecord.getData()));

        return returnList;
    }

    /**
     * Adds the provided transaction to the database
     * @param transactionToAdd {@link Transaction} to be added to the database
     * @return String containing the UUID of the added transaction
     */
    public String addTransaction(Transaction transactionToAdd) {
        UUID id = UUID.randomUUID();
        transactionToAdd.setTransId(id);

        TransactionRecord trToPersist = new TransactionRecord(id, transactionToAdd);

        try {
            em.getTransaction().begin();
            em.persist(trToPersist);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            //TODO add logging
            System.out.println(ex.getMessage());
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
        List<String> returnList = new ArrayList<>();

        //TODO: I'm sure there's a better way to do this - but not a faster one!
        for(Transaction thisTrans : transList) {
            returnList.add(this.addTransaction(thisTrans));
        }

        return returnList;
    }

    /**
     * Updates the transaction matching the provided transaction id with the provided {@link Transaction}
     * @param transactionId String containing the UUID of the transaction
     * @param transToUpdate {@link Transaction} the transaction object to save
     * @return boolean on whether or not the save was successful
     */
    public boolean updateTransaction(String transactionId, Transaction transToUpdate) {
        TransactionRecord transactionToPersist = new TransactionRecord(UUID.fromString(transactionId), transToUpdate);

        try {
            em.getTransaction().begin();
            em.merge(transactionToPersist);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
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
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from TransactionRecord t where t.id = '" + idToDelete + ';').executeUpdate();
        em.getTransaction().commit();

        return returnVal;
    }

    /**
     * Deletes all of the transactions in the database
     * @return integer capturing the number of records deleted
     */
    public int deleteAllTransactions() {
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from TransactionQuery t").executeUpdate();
        em.getTransaction().commit();

        return returnVal;
    }
}
