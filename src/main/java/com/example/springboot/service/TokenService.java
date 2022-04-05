package com.example.springboot.service;

import com.example.springboot.exception.TokenNotFoundException;
import com.example.springboot.object.Token;
import com.example.springboot.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class TokenService {
    private TokenRepository tokenRepository;
    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Collection<Token> getAllTokens() {
        Iterable<Token> tokenIter = tokenRepository.findAll();
        ArrayList<Token> tokenList = new ArrayList<Token>();
        tokenIter.forEach(item -> {
            tokenList.add(item);
        });
        return tokenList;
    }

    public Token getTokenById(Long id) {
        Token token =  tokenRepository.findOne(id);
        if(token == null) {
            throw new TokenNotFoundException(id);
        }
        return token;
    }

    public Token getTokenByName(String name) {
        return tokenRepository.findByName(name)
                .orElseThrow(() -> new TokenNotFoundException(name));
    }

    public Boolean findTokenByName(String name) {
        Optional<Token> findResult = tokenRepository.findByName(name);
        if (findResult.isPresent()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Token addToken(Token token) {
        return tokenRepository.save(token);

    }

    public Token updateToken(Token token) {
        getTokenById(token.getId());
        return tokenRepository.save(token);
    }

    public void deleteToken(String name) {
        Token token = getTokenByName(name);
        tokenRepository.delete(token.getId());
    }

}
