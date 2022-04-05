package com.example.springboot.repository;

import com.example.springboot.object.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByName(String name);
}
