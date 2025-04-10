package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.util.JPAUtil;
import br.com.compass.bankchallenge.util.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class AuthService {

	public User login(String email, String password) {
	    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
	    EntityTransaction tx = null;

	    try {
	        User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
	                      .setParameter("email", email)
	                      .getSingleResult();

	        if (user.isBlocked()) {
	            System.out.println("Account is locked. Please contact a manager.");
	            return null;
	        }

	        String hashedInput = SecurityUtil.hashPassword(password);
	        tx = em.getTransaction();
	        tx.begin();

	        if (hashedInput.equals(user.getPassword())) {
	            user.setFailedLoginAttempts(0);
	            tx.commit();
	            return user;
	        } else {
	            int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
	            user.setFailedLoginAttempts(attempts);
	            if (attempts >= 3) {
	                user.setBlocked(true);
	                System.out.println("Account locked after 3 failed attempts.");
	            } else {
	                System.out.println("Incorrect password. Attempt " + attempts + " of 3.");
	            }
	            tx.commit();
	            return null;
	        }
	    } catch (NoResultException e) {
	        System.out.println("User not found");
	        return null;
	    } finally {
	        if (em.isOpen()) {
	            em.close();
	        }
	    }
	}

	public void register(User user) {
	    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
	    EntityTransaction tx = em.getTransaction();

	    try {
	        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
	                       .setParameter("email", user.getEmail())
	                       .getSingleResult();
	        if (count > 0) {
	            System.out.println("Email already registered.");
	            return;
	        }

	        tx.begin();
	        user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
	        em.persist(user);
	        tx.commit();
	        System.out.println("User registered successfully: " + user.getEmail());
	    } catch (Exception e) {
	        if (tx.isActive()) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	        System.out.println("Error during registration.");
	    } finally {
	        em.close();
	    }
	}
    
}
