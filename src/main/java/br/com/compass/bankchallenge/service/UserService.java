package br.com.compass.bankchallenge.service;

import java.util.List;

import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.repository.UserRepository;

public class UserService {

    private UserRepository userRepository = new UserRepository();

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findBlockedUsers() {
        return new UserRepository().findByBlocked(true);
    }

    public User findById(Long id) {
        return new UserRepository().findById(id);
    }

    public void update(User user) {
        new UserRepository().update(user);
    }
    
}
