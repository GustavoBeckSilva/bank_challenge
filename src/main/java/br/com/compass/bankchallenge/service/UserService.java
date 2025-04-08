package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.repository.UserRepository;

public class UserService {

    private UserRepository userRepository = new UserRepository();

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
}
