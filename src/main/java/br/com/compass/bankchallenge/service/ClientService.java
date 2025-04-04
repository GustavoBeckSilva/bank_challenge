package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.repository.ClientRepository;

public class ClientService {

	private ClientRepository clientRepository = new ClientRepository();

    public void registerClient(String name, String cpf, String phone) {
        Client client = new Client();
        client.setName(name);
        client.setCpf(cpf);
        client.setPhone(phone);
        clientRepository.save(client);
    }
}
