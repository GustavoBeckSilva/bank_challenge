package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.util.InputValidatorUtil;
import br.com.compass.bankchallenge.util.JPAUtil;
import br.com.compass.bankchallenge.util.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class AuthService {

	public User login(String input, String password) {
	    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
	    EntityTransaction tx = null;
	    try {
	        User user = null;

	        if (InputValidatorUtil.isValidCpf(input)) {
	            user = em.createQuery("SELECT c FROM Client c WHERE c.cpf = :input", Client.class)
	                     .setParameter("input", input)
	                     .getSingleResult();
	        }

	        else if (InputValidatorUtil.isValidEmail(input)) {
	            user = em.createQuery("SELECT m FROM Manager m WHERE m.email = :input", Manager.class)
	                     .setParameter("input", input)
	                     .getSingleResult();
	        } else {
	            System.out.println("Invalid identification format. Enter a valid CPF or email.\r\n");
	            return null;
	        }

	        if (user.isBlocked()) {
	            System.out.println("\n\nThis account is locked. Please contact a manager.");
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
	    } catch (Exception e) {
	    	if (tx != null && tx.isActive())
	            tx.rollback();
	        
	        e.printStackTrace();
	        return null;
		}
	    finally {
	        if (em.isOpen()) {
	            em.close();
	        }
	    }
	}

	public void register(User user) {
	    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
	    EntityTransaction tx = em.getTransaction();

	    try {
	    	boolean userExists;

	        if (user instanceof Client client) {
	            userExists = em.createQuery("SELECT COUNT(c) FROM Client c WHERE c.cpf = :cpf", Long.class)
	                           .setParameter("cpf", client.getCpf())
	                           .getSingleResult() > 0;

	            if (userExists) {
	                System.out.println("CPF already registered for a client.");
	                return;
	            }
	        } 
	        
	        else if (user instanceof Manager manager) {
	            userExists = em.createQuery("SELECT COUNT(m) FROM Manager m WHERE m.email = :email", Long.class)
	                           .setParameter("email", manager.getEmail())
	                           .getSingleResult() > 0;

	            if (userExists) {
	                System.out.println("Email already registered for a manager.");
	                return;
	            }
	        } 
	        
	        else {
	            System.out.println("Unsupported user type.");
	            return;
	        }

	        tx.begin();
	        user.setPassword(SecurityUtil.hashPassword(user.getPassword()));
	        em.persist(user);
	        tx.commit();
	        System.out.println("User registered successfully");
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
	
	public void registerManager(String name, String email, String password) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            System.out.println("Name, email, and password are required.");
            return;
        }

        Manager manager = new Manager();
        manager.setName(name);
        manager.setEmail(email);
        manager.setPassword(password);
        manager.setAccessLevel(AccessLevel.MANAGER);

        register(manager);
    }
    
}
