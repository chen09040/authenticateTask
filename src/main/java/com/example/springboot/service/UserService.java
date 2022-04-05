package com.example.springboot.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.object.User;
import com.example.springboot.exception.UserNotFoundException;
import com.example.springboot.repository.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<User> getAllUsers() {
        Iterable<User> userIter = userRepository.findAll();
        ArrayList<User> userList = new ArrayList<User>();
        userIter.forEach(item -> {
            userList.add(item);
        });
        return userList;
    }

    public User getUserById(Long id) {
        User user =  userRepository.findOne(id);
        if(user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException(name));
    }

    public Boolean findUserByName(String name) {
        Optional<User> findResult = userRepository.findByName(name);
        if (findResult.isPresent()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public User addUser(User user) {
        // Encrypt the password
        String cipherPassword = new String(Base64.getEncoder().encode(user.getPassword().getBytes()));
        user.setPassword(cipherPassword);
        return userRepository.save(user);

    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userRepository.save(user);
    }

    public void deleteUser(String name) {
        User user = getUserByName(name);
        userRepository.delete(user.getId());
    }

}
