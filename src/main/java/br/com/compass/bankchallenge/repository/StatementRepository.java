package br.com.compass.bankchallenge.repository;

import java.util.List;

import br.com.compass.bankchallenge.domain.Statement;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class StatementRepository {
	
	public void save(Statement statement) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(statement);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

	public List<Statement> findByAccountId(Long accountId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Statement s WHERE s.account.id = :accountId", Statement.class)
                     .setParameter("accountId", accountId)
                     .getResultList();
        } finally {
            em.close();
        }
    }
	    
}
