package br.com.compass.bankchallenge.config;

import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.util.JPAUtil;
import br.com.compass.bankchallenge.util.SecurityUtil;
import jakarta.persistence.EntityManager;

public class DatabaseInitializer {
	
	 public static void loadInitialManager() {
	        
		 EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

	        try {
	            em.getTransaction().begin();

	            Long count = em.createQuery("SELECT COUNT(m) FROM Manager m", Long.class).getSingleResult();

	            if (count == 0) {
	                Manager manager = new Manager();
	                manager.setName("Root Manager");
	                manager.setEmail("rootmanager@bankemail.com");
	                manager.setPassword(SecurityUtil.hashPassword("123456"));
	                manager.setAccessLevel(AccessLevel.MANAGER);
	                em.persist(manager);

	                System.out.println("Root Manager account created: rootmanager@bankemail.com / 123456");
	            }

	            em.getTransaction().commit();
	            
	        } catch (Exception e) {
	            em.getTransaction().rollback();
	            e.printStackTrace();
	        } finally {
	            em.close();
	        }
	    }
	}
