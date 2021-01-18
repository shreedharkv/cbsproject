package com.dfq.coeffi.cbs.user.service;
/**
 * @Auther : H Kapil Kumar
 * @Date : May-18
 */

import com.dfq.coeffi.cbs.exception.DuplicateUserException;
import com.dfq.coeffi.cbs.user.entity.Role;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.repository.RoleRepository;
import com.dfq.coeffi.cbs.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<User> users() {
        return userRepository.findByActive(true);
    }

    @Override
    public User getUser(long id) {
        return userRepository.getOne(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findByActive(true);
    }

    @Override
    public Role getRole(long id) {
        return roleRepository.findOne(id);
    }

    @Override
    public Role saveRole(Role role) {
        role.setActive(true);
        return roleRepository.save(role);
    }

    @Override
    public void isUserExists(String email) {
        Optional<User> userDb = userRepository.findUserByEmail(email);
        if (userDb.isPresent()) {
            log.warn("User already exists");
            throw new DuplicateUserException("User", "email " + email);
        }
    }

    @Override
    public User saveUser(User user) {
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }
}