package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.repository.ManagerRepository;
import br.com.compass.bankchallenge.repository.UserRepository;

public class ManagerService {

    private ManagerRepository managerRepository = new ManagerRepository();
    private UserRepository userRepository = new UserRepository();

    public void registerManager(String name, String email, String password) {
        
    	if (userRepository.findByEmail(email) != null) 
            throw new IllegalArgumentException("There is already a user with the email provided.");
        
        
        Manager manager = new Manager();
        manager.setName(name);
        manager.setEmail(email);
        manager.setPassword(password);

        managerRepository.save(manager);
    }
}
