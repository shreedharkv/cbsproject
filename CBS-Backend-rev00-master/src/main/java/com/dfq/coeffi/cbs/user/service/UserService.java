package com.dfq.coeffi.cbs.user.service;

import com.dfq.coeffi.cbs.user.entity.Role;
import com.dfq.coeffi.cbs.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author H Kapil Kumar
 */
public interface UserService {

    List<User> users();

    User getUser(long id);

    User saveUser(User user);

    void deleteUser(long id);

    List<Role> getRoles();

    Role getRole(long id);

    Role saveRole(Role role);

    void isUserExists(String email);
}