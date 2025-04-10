package br.com.compass.bankchallenge.repository;

import java.util.List;

import br.com.compass.bankchallenge.domain.RefundRequest;
import br.com.compass.bankchallenge.domain.enums.RefundStatus;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class RefundRequestRepository {

	public void save(RefundRequest refundRequest) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(refundRequest);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public RefundRequest findById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(RefundRequest.class, id);
        } finally {
            em.close();
        }
    }

    public List<RefundRequest> findByClientId(Long clientId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<RefundRequest> query = em.createQuery(
                "SELECT r FROM RefundRequest r WHERE r.client.id = :clientId", RefundRequest.class);
            query.setParameter("clientId", clientId);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<RefundRequest> findByStatus(RefundStatus status) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<RefundRequest> query = em.createQuery(
                "SELECT r FROM RefundRequest r WHERE r.status = :status", RefundRequest.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void update(RefundRequest refundRequest) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(refundRequest);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
