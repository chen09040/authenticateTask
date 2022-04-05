package com.example.springboot.object;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Base64;
import java.util.Set;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String password;

    @ElementCollection
    private Set<String> roles;

    public User() { //for jpa only
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
        // return new String(Base64.getDecoder().decode(password.getBytes()));
    }
    public void setPassword(String password) {
        this.password = password;
        // this.password = new String(Base64.getEncoder().encode(password.getBytes()));
    }

    public Set<String> getRoles() {
        return roles;
    }
    public void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", password=" + password + ", decode password=" + getPassword() + ", roles=" + roles.toString() + "]";
    }


}