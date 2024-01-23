package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.User;
import com.vranic.zavrsnirad.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers(){
        return userRepository.getAllUsers();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public void save(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public List<User> findUserByUsername(String username){
       return userRepository.getUsersByUsername(username);
    }
}
