package com.example.springboot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.springboot.object.User;
import com.example.springboot.service.UserService;


@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class SpringbootApplication {

//    @Bean
//    CommandLineRunner init(UserService userService) {
//        // add 5 new users after app is started
//        return (evt) ->
//                Arrays.asList("czb1,czb2".split(","))
//                        .forEach(item -> {
//                            Double r = Math.random();
//                            String r_str = r.toString();
//                            User user = new User(item, r_str);
//                            userService.addUser(user);
//                        });
//    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
