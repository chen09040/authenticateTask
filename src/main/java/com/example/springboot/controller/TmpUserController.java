//package com.example.springboot.controller;
//
//import com.example.springboot.object.User;
//import com.example.springboot.rest.RestResultResponse;
//import com.example.springboot.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collection;
//
//@RestController
//@RequestMapping("/user")
//public class TmpUserController {
//	private UserService userService;
//
//	@Autowired
//	public UserController(UserService userService) {
//		this.userService = userService;
//	}
//
//	@RequestMapping(method = RequestMethod.GET)
//    public Collection<User> getUsers() {
//		return userService.getAllUsers();
//    }
//
//	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
//	@ResponseStatus(HttpStatus.CREATED)
//    public User addUser(@RequestBody User user) {
//		if(user.getName() == null || user.getName().isEmpty()) {
//			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
//		}
//		if(user.getPassword() == null) {
//			throw new IllegalArgumentException("Parameter 'password' must not be null or empty");
//		}
//        return userService.addUser(user);
//    }
//
//	@RequestMapping(value="/{id}", method = RequestMethod.GET)
//    public User getUser(@PathVariable("id") Long id) {
//        return userService.getUserById(id);
//    }
//
//	@RequestMapping(value="/{id}", method = RequestMethod.PUT, consumes = "application/json")
//    public User updateUser(@PathVariable("id") Long id, @RequestBody User user) {
//		if(user.getName() == null && user.getPassword() == null) {
//			throw new IllegalArgumentException("Parameter 'name' and 'password' must not both be null");
//		}
//		user.setId(id);
//        return userService.updateUser(user);
//    }
//
//	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)
//    public RestResultResponse deleteUser(@PathVariable("id") Long id) {
//		userService.deleteUser(id);
//		return new RestResultResponse(true);
//    }
//
//}
