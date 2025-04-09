package br.com.compass.bankchallenge.service;

import java.time.LocalDate;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.repository.ClientRepository;
import br.com.compass.bankchallenge.repository.UserRepository;

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

}
