package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.util.SecurityUtil;
import jakarta.persistence.*;

public class AuthService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("exemplo-jpa");

    public User login(String email, String password) {
        EntityManager em = emf.createEntityManager();
        User user = null;

        try {
            user = em.createQuery("SELECT c FROM Client c WHERE c.email = :email", User.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("❌ User not found");
            em.close();
            return null;
        }

        if (user.isBlocked()) {
            System.out.println("Account is locked. Please contact a manager.");
            em.close();
            return null;
        }

        String hashedInput = SecurityUtil.hashPassword(password);

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        if (hashedInput.equals(user.getPassword())) {
            user.setFailedLoginAttempts(0);
            tx.commit();
            em.close();
            return user;
        } else {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 3) {
                user.setBlocked(true);
                System.out.println("Account locked after 3 failed attempts.");
            } else {
                System.out.println("Incorrect password. Attempt " + attempts + " of 3.");
            }
            tx.commit();
            em.close();
            return null;
        }
    }

    public void register(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            user.setPassword(SecurityUtil.hashPassword(user.getPassword())); // hash antes de persistir
            em.persist(user);
            transaction.commit();
            System.out.println("User registered successfully: " + user.getEmail());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("Error during registration.");
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}
