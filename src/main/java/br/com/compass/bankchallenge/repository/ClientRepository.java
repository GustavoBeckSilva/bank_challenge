package br.com.compass.bankchallenge.repository;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class ClientRepository {
	
    public void save(Client client) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();
        em.close();
    }
	
}
