package br.com.compass.bankchallenge.service;

import java.time.LocalDate;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.repository.UserRepository;
import br.com.compass.bankchallenge.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class ClientService {

	private UserRepository userRepository = new UserRepository();
	private AuthService authService = new AuthService();

	public void registerClient(String name, String email, String password, String cpf, String phone, LocalDate birthDate) {

		if (userRepository.findByEmail(email) != null)
			throw new IllegalArgumentException("There is already a user with the email provided.");

		Client client = new Client(cpf, phone, birthDate);
		client.setName(name);
		client.setEmail(email);
		client.setPassword(password);
		client.setAccessLevel(AccessLevel.CLIENT);

		authService.register(client);
	}
	
	public Client findByCpf(String cpf) {
	    EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
	    try {
	        TypedQuery<Client> query = em.createQuery(
	            "SELECT c FROM Client c WHERE c.cpf = :cpf", Client.class);
	        query.setParameter("cpf", cpf);
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } finally {
	        em.close();
	    }
	}

}
