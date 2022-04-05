package com.example.springboot.controller;

import com.example.springboot.object.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class HelloController {

    @Value("${name}")
    private String name;

    @Value("${food}")
    private String food;

    @RequestMapping("/hello")
    public String hello() {
        return name + " first Spring Boot project: "+ food;
    }

    @RequestMapping("/addUser")
    public String createUser(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = new User(username, password);
        return "create user successfully...\n" + user.toString();
    }
}

