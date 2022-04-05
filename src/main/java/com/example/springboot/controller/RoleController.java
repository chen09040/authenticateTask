package com.example.springboot.controller;

import com.example.springboot.object.Role;
import com.example.springboot.rest.RestResultResponse;
import com.example.springboot.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/role")
public class RoleController {

    private RoleService roleService;

    // init role service, the actual method is implemented in role service
    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // get all the roles in DB
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Role> getRoles() {
        return roleService.getAllRoles();
    }

    // 3、create role by name, fail if exist
    // curl -X POST -H "Content-Type: application/json" -d '{"name": "role123"}' "http://localhost:8088/role/"
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Role addRole(@RequestBody Role role) {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("Parameter 'name' must not be null or empty");
        }
        // should fail if role already exist
        if (roleService.findRoleByName(role.getName())) {
            throw new IllegalArgumentException("role " + role.getName() + " already exist");
        }
        return roleService.addRole(role);
    }

    // 4、delete role by name and should fail if not exist
    // curl -X DELETE -H "Content-Type: application/json" "http://localhost:8088/role/role123"
    @RequestMapping(value="/{name}", method = RequestMethod.DELETE)
    public RestResultResponse deleteRole(@PathVariable("name") String name) {
        roleService.deleteRole(name);
        return new RestResultResponse(true);
    }

    // get role by name
    // http://localhost:8088/role/czb123
    @RequestMapping(value="/{name}", method = RequestMethod.GET)
    public Role getRole(@PathVariable("name") String name) {
        return roleService.getRoleByName(name);
    }

}
