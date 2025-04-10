package br.com.compass.bankchallenge.repository;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class AccountRepository {

    public Account save(Account account) {
    	EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            
        	em.getTransaction().begin();
            
            if (account.getId() == null)
                em.persist(account);
            
            else
                account = em.merge(account);
            
            em.getTransaction().commit();
            return account;
            
        } finally {
            em.close();
        }
    }

    public Account findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Account.class, id);
        } finally {
            em.close();
        }
    }

    public Account findByAccountNumber(String accountNumber) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

}
