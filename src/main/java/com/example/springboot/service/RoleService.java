package com.example.springboot.service;

import com.example.springboot.exception.RoleNotFoundException;
import com.example.springboot.object.Role;
import com.example.springboot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Collection<Role> getAllRoles() {
        Iterable<Role> roleIter = roleRepository.findAll();
        ArrayList<Role> roleList = new ArrayList<Role>();
        roleIter.forEach(item -> {
            roleList.add(item);
        });
        return roleList;
    }

    public Role getRoleById(Long id) {
        Role role =  roleRepository.findOne(id);
        if(role == null) {
            throw new RoleNotFoundException(id);
        }
        return role;
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }

    public Boolean findRoleByName(String name) {
        Optional<Role> findResult = roleRepository.findByName(name);
        if (findResult.isPresent()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Role addRole(Role role) {
        return roleRepository.save(role);

    }

    public Role updateRole(Role role) {
        getRoleById(role.getId());
        return roleRepository.save(role);
    }

    public void deleteRole(String name) {
        Role role = getRoleByName(name);
        roleRepository.delete(role.getId());
    }

}
