package br.com.compass.bankchallenge.repository;

import java.util.List;

import br.com.compass.bankchallenge.domain.Operation;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class OperationRepository {
	
	public void save(Operation operation) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(operation);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Operation findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Operation.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Operation> findByAccountId(Long accountId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Operation> query = em.createQuery(
                "SELECT o FROM Operation o WHERE o.account.id = :accountId", Operation.class);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
	
}
