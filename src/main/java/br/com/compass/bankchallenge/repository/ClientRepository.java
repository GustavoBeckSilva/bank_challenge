package br.com.compass.bankchallenge.repository;

import br.com.compass.bankchallenge.domain.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ClientRepository {
	
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("exemplo-jpa");

    public void save(Client client) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();
        em.close();
    }
	
}
