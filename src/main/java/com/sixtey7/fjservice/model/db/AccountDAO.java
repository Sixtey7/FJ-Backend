package com.sixtey7.fjservice.model.db;

import com.sixtey7.fjservice.model.Account;
import com.sixtey7.fjservice.model.AccountRecord;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO Class for the Accounts table
 */
@Dependent
public class AccountDAO {

    /**
     * EMF used to create the entity manager
     */
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");

    /**
     * Entity Manager to be used for this DAO
     */
    EntityManager em = emf.createEntityManager();

    /**
     * Returns all of the accounts in the database
     * @return {@link List} containing all of the {@link Account} in the database
     */
    public List<Account> getAllAccounts() {
        List<AccountRecord> records = em.createQuery("Select a from AccountRecord a", AccountRecord.class).getResultList();

        List<Account> returnList = new ArrayList<>();
        for (AccountRecord ar : records) {
            returnList.add(ar.getData());
        }

        return returnList;
    }

    /**
     * Returns the data for the specified account
     * @param accountId String representing the UUID of the account to get data for
     * @return {@link Account} object that was requested (null if not present)
     */
    public Account getAccount(String accountId) {
        try {
            AccountRecord ar = (AccountRecord) em.createQuery("Select a from AccountRecord a where a.id = '" + accountId + "'").getSingleResult();

            return ar.getData();
        }
        catch (NoResultException nre) {
            //TODO: Probably shouldn't rely on catching an error here and handle this smoother
            return null;
        }
    }

    /**
     * Adds the provided account to the database
     * @param accountToAdd {@link Account} the account object to add to the database
     * @return String containing the UUID of the added account
     */
    public String addAccount(Account accountToAdd) {

        UUID newId = UUID.randomUUID();
        accountToAdd.setId(newId);

        AccountRecord arToPersist = new AccountRecord(newId, accountToAdd);

        try {
            em.getTransaction().begin();
            em.persist(arToPersist);
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            //TODO: Add Logging
            System.out.println(ex.getMessage());
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
    public boolean updateAccount(String accountId, Account accountToUpdate) {
        AccountRecord accountToPersist = new AccountRecord(UUID.fromString(accountId), accountToUpdate);

        try {
            em.getTransaction().begin();
            em.merge(accountToPersist);
            em.getTransaction().commit();
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }

        return true;
    }

    /**
     * Deletes the account matching the provided UUID
     * @param idToDelete String containing the UUID of the account to delete
     * @return integer capturing the number of records deleted (nominally 0 or 1)
     */
    public int deleteAccount(String idToDelete) {
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from AccountRecord a where a.id = '" + idToDelete + "';").executeUpdate();
        em.getTransaction().commit();

        return returnVal;
    }

    /**
     * Deletes all of the accounts in the database
     * @return integer capturing the number of records deleted
     */
    public int deleteAllAccounts() {
        em.getTransaction().begin();
        int returnVal = em.createQuery("Delete from AccountRecord a").executeUpdate();

        return returnVal;
    }



}
