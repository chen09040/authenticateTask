package com.example.springboot.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

import com.example.springboot.object.User;


public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByName(String name);
}
