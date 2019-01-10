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

@Dependent
public class AccountDAO {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("FJDB");
    EntityManager em = emf.createEntityManager();

    public List<Account> getAllAccounts() {
        List<AccountRecord> records = em.createQuery("Select a from AccountRecord a", AccountRecord.class).getResultList();

        List<Account> returnList = new ArrayList<>();
        for (AccountRecord ar : records) {
            returnList.add(ar.getData());
        }

        return returnList;
    }

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

}
