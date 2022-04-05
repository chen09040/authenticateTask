package com.example.springboot.controller;

import com.example.springboot.object.Role;
import com.example.springboot.object.Token;
import com.example.springboot.object.User;
import com.example.springboot.rest.RestResultResponse;
import com.example.springboot.service.TokenService;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.ElementCollection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	private TokenService tokenService;

	// init user service, the actual method is implemented in user service
	@Autowired
	public UserController(UserService userService, TokenService tokenService) {
		this.userService = userService;
		this.tokenService = tokenService;
	}

	// get all the users in DB
	@RequestMapping(method = RequestMethod.GET)
    public Collection<User> getUsers() {
		return userService.getAllUsers();
    }

    // 1、create user by name and password, fail if exist
	// curl -X POST -H "Content-Type: application/json" -d '{"name": "czb123","password": "Hello Việt Nam"}' "http://localhost:8088/user/"
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
		if (user.getName() == null || user.getName().isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
		}
		if (user.getPassword() == null) {
			throw new IllegalArgumentException("Parameter 'password' must not be null or empty");
		}
		// should fail if user already exist
		if (userService.findUserByName(user.getName())) {
			throw new IllegalArgumentException("user " + user.getName() + " already exist");
		}
        return userService.addUser(user);
    }

    // 2、delete user by name and should fail if not exist
	// curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/user/czb123"
	@RequestMapping(value="/{name}", method = RequestMethod.DELETE)
	public RestResultResponse deleteUser(@PathVariable("name") String name) {
		userService.deleteUser(name);
		return new RestResultResponse(true);
	}

	// 5、add role to user by user name and role name, nothing happen if role already associate with user
	// curl -X PUT -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/czb123/addrole"
	@RequestMapping(value="/{name}/addrole", method = RequestMethod.PUT, consumes = "application/json")
	public User addRoleToUser(@PathVariable("name") String name, @RequestBody Role role) {
		if (userService.findUserByName(name) == Boolean.FALSE) {
			throw new IllegalArgumentException("user " + name + " not exist");
		}
		if (role.getName() == null || role.getName().isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' of role must not be null or empty");
		}

		User user = userService.getUserByName(name);
		// the roles of user is Set<String>, no need to deduplicate
		user.addRole(role.getName());
		return userService.updateUser(user);
	}

	// 6、authenticate by user name and password, return 2-hour token or error if not found
	// curl -X POST -H "Content-Type: application/json" -d '{"name": "czb123","password": "Hello Việt Nam"}' "http://localhost:8088/user/authenticate"
	@RequestMapping(value="/authenticate", method = RequestMethod.POST, consumes = "application/json")
	public RestResultResponse authenticateUser(@RequestBody User user) {
		if (user.getName() == null || user.getName().isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
		}
		if (user.getPassword() == null) {
			throw new IllegalArgumentException("Parameter 'password' must not be null or empty");
		}

		User currentUser = userService.getUserByName(user.getName());
		String currentUserPassword = new String(Base64.getDecoder().decode(currentUser.getPassword().getBytes()));
		System.out.println("currentUserPassword: " + currentUserPassword);
		System.out.println("user.getPassword(): " + user.getPassword());
		if (!currentUserPassword.equals(user.getPassword())) {
			throw new IllegalArgumentException("user 'password' is wrong");
		}

		Long currentTimestamp = System.currentTimeMillis();
		String concatInfo = currentUser.getName() + "," + currentUser.getPassword() + "," + currentTimestamp.toString();
		String cipherConcatInfo = new String(Base64.getEncoder().encode(concatInfo.getBytes()));
		System.out.println("concatInfo: " + concatInfo);
		System.out.println("cipherConcatInfo: " + cipherConcatInfo);

		// add the cipherConcatInfo to tokenRepository
		Token token = new Token(cipherConcatInfo);
		System.out.println("token: " + token.toString());
		System.out.println("token.getName(): " + token.getName());
		System.out.println("tokenService.findTokenByName(token.getName()): " + tokenService.findTokenByName(token.getName()));

		Collection<Token> tokenList = tokenService.getAllTokens();
		System.out.println("tokenService: tokenList.size(): " + tokenList.size());
		tokenList.forEach(item -> {
			System.out.println("tokenService: token: " + item.getName());
		});

		// should fail if user already exist
		if (tokenService.findTokenByName(token.getName()) == Boolean.TRUE) {
			throw new IllegalArgumentException("token " + token.getName() + " already exist");
		}
		tokenService.addToken(token);
		return new RestResultResponse(true, token);
	}


	// 7、invalidate by token, return nothing and the token is no longer valid after the call
	// curl -X PUT -H "Content-Type: application/json" "http://localhost:8088/user/invalidate/{token}"
	// curl -X PUT -H "Content-Type: application/json" "http://localhost:8088/user/invalidate/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTQ4OTgyNjIx"
	@RequestMapping(value="/invalidate/{name}", method = RequestMethod.PUT, consumes = "application/json")
	public RestResultResponse invalidateToken(@PathVariable("name") String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
		}

		// should fail if user already exist
		if (tokenService.findTokenByName(name) == Boolean.FALSE) {
			throw new IllegalArgumentException("token " + name + " does not exist");
		}
		tokenService.deleteToken(name);

		Collection<Token> tokenList = tokenService.getAllTokens();
		System.out.println("after invalidate, tokenService: tokenList.size(): " + tokenList.size());
		tokenList.forEach(item -> {
			System.out.println("tokenService: token: " + item.getName());
		});

		return new RestResultResponse(true, name);
	}

	// 8、check role by token and role, return true if user identified by token belong to role, false otherwise, error if token expired
	// curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/checkrole/{token}"
	// curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/user/checkrole/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNDM3NDM1"
	@RequestMapping(value="/checkrole/{name}", method = RequestMethod.POST, consumes = "application/json")
	public RestResultResponse checkRole(@PathVariable("name") String name, @RequestBody Role role) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
		}

		// decode the token, "{name},{password},{timestamp}"
		String rawConcatInfo = new String(Base64.getDecoder().decode(name.getBytes()));
		String[] parsedConcatInfo = rawConcatInfo.split(",");
		if (parsedConcatInfo.length != 3) {
			throw new IllegalArgumentException(
					"length of rawConcatInfo: " + rawConcatInfo + " is not equal to 3");
		}

		String username = parsedConcatInfo[0];
		String password = new String(Base64.getDecoder().decode(parsedConcatInfo[1].getBytes()));
		Long timestamp = Long.parseLong(parsedConcatInfo[2]);
		System.out.println("username: " + username + "password: " + password + "timestamp: " + timestamp);

		User user = userService.getUserByName(username);
		String userPassword = new String(Base64.getDecoder().decode(user.getPassword().getBytes()));
		if (!userPassword.equals(password)) {
			return new RestResultResponse(false,
					"input password: " + password + " not equal to userPassword: " + userPassword);
		}

		Long currentTimestamp = System.currentTimeMillis();
		if (currentTimestamp - timestamp > 2 * 3600 * 1000) {  // 2 hours
			return new RestResultResponse(false,
					"token is beyond 2 hours, inputTimestamp: " + timestamp + " currentTimestamp: " + currentTimestamp);
		}

		// return true if user identified by token belong to role
		Set<String> userRoles = user.getRoles();
		System.out.println("userRoles: " + userRoles + "input role name: " + role.getName());

		if (!userRoles.contains(role.getName())) {
			return new RestResultResponse(false,
					"user identified by token is NOT belong to role. " + "userRoles: " + userRoles + "input role name: " + role.getName());
		}

		return new RestResultResponse(true,
				"success! user identified by token is belong to role. " + "userRoles: " + userRoles + "input role name: " + role.getName());
	}

	// 9、all roles by token, return all roles for user, error if the token is invalid
	// curl -X GET -H "Content-Type: application/json" "http://localhost:8088/user/allroles/{token}"
	// curl -X GET -H "Content-Type: application/json" "http://localhost:8088/user/allroles/Y3piMTIzLFNHVnNiRzhnVm1uaHU0ZDBJRTVoYlE9PSwxNjQ5MTYzNzY0Nzcy"
	@RequestMapping(value="/allroles/{name}", method = RequestMethod.GET, consumes = "application/json")
	public RestResultResponse allRoles(@PathVariable("name") String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
		}

		// decode the token, "{name},{password},{timestamp}"
		String rawConcatInfo = new String(Base64.getDecoder().decode(name.getBytes()));
		String[] parsedConcatInfo = rawConcatInfo.split(",");
		if (parsedConcatInfo.length != 3) {
			throw new IllegalArgumentException(
					"length of rawConcatInfo: " + rawConcatInfo + " is not equal to 3");
		}

		String username = parsedConcatInfo[0];
		String password = new String(Base64.getDecoder().decode(parsedConcatInfo[1].getBytes()));
		Long timestamp = Long.parseLong(parsedConcatInfo[2]);
		System.out.println("username: " + username + "password: " + password + "timestamp: " + timestamp);

		User user = userService.getUserByName(username);
		String userPassword = new String(Base64.getDecoder().decode(user.getPassword().getBytes()));
		if (!userPassword.equals(password)) {
			return new RestResultResponse(false,
					"input password: " + password + " not equal to userPassword: " + userPassword);
		}

		Long currentTimestamp = System.currentTimeMillis();
		if (currentTimestamp - timestamp > 2 * 3600 * 1000) {  // 2 hours
			return new RestResultResponse(false,
					"token is beyond 2 hours, inputTimestamp: " + timestamp + " currentTimestamp: " + currentTimestamp);
		}

		// all roles by token, return all roles for user
		Set<String> userRoles = user.getRoles();
		System.out.println("userRoles: " + userRoles);

		return new RestResultResponse(true, userRoles);
	}

	// get user by name
    // http://localhost:8088/user/czb123
	@RequestMapping(value="/{name}", method = RequestMethod.GET)
    public User getUser(@PathVariable("name") String name) {
        return userService.getUserByName(name);
    }

	@RequestMapping(value="/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public User updateUser(@PathVariable("id") Long id, @RequestBody User user) {
		if(user.getName() == null && user.getPassword() == null) {
			throw new IllegalArgumentException("Parameter 'name' and 'password' must not both be null");
		}
		user.setId(id);
        return userService.updateUser(user);
    }

}
