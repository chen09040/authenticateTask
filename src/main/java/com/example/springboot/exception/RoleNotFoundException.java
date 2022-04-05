package com.example.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(Long roleId) {
        super("could not find role with id '" + roleId + "'.");
    }

    public RoleNotFoundException(String roleName) {
        super("could not find role name '" + roleName + "'.");
    }
}
