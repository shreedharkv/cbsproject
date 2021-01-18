/**
 *
 */
package com.dfq.coeffi.cbs.user.api;

import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.user.dto.PasswordDto;
import com.dfq.coeffi.cbs.user.dto.UserConverter;
import com.dfq.coeffi.cbs.user.dto.UserDto;
import com.dfq.coeffi.cbs.user.entity.Role;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author H Kapil Kumar
 */

@Slf4j
@RestController
public class UserApi extends BaseController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final ApplicationLogService applicationLogService;

    @Autowired
    private UserApi(final UserService userService, final UserConverter userConverter, final ApplicationLogService applicationLogService) {
        this.userService = userService;
        this.userConverter = userConverter;
        this.applicationLogService = applicationLogService;
    }


    @GetMapping("/logged-user")
    public User user(Principal principal) {
        InetAddress IP = null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        User user = userService.getUser(Integer.parseInt(principal.getName()));
        if (user != null) {
            applicationLogService.recordApplicationLog(user.getFirstName(), user.getFirstName() + " signed in successfully", "Sign In", user.getId());
        }
        return user;
    }

    @GetMapping("/logged-out")
    public ResponseEntity<User> userLoggedOut(Principal principal) {
        User loggedUser = getLoggedUser(principal);
        if (loggedUser != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), loggedUser.getFirstName() + " signed out successfully", "Sign Out", loggedUser.getId());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> loadUsers() {
        return userService.users();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User userObj = userService.getUser(id);
        if (userObj == null) {
            log.warn("Unable to deactivate user with ID : {} not found", id);
            throw new EntityNotFoundException(User.class.getSimpleName());
        }
        return new ResponseEntity<>(userObj, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
        User user = userConverter.toEntity(userDto);
        User persistedObject = userService.saveUser(user);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @PostMapping("/update-user")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {

        Role role = userService.getRole(user.getRoleId());
        user.setRoles(Arrays.asList(role));
        User persistedObject = userService.saveUser(user);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        User user = userService.getUser(id);
        if (user == null) {
            log.warn("Unable to deactivate user with ID : {} not found", id);
            throw new EntityNotFoundException(User.class.getSimpleName());
        }
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user-roles")
    public ResponseEntity<List<Role>> getRole() {
        List<Role> roles = userService.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            throw new EntityNotFoundException("roles");
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping("/user-role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        Role persistedObject = userService.saveRole(role);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @PostMapping("/user/password-reset")
    public ResponseEntity<User> resetUserPassword(@Valid @RequestBody PasswordDto passwordDto, Principal principal) {

        User loggedUser = getLoggedUser(principal);

        User persistedUser = null;
        User user = userService.getUser(passwordDto.getUserId());

        if (user != null) {
            user.setPassword(passwordDto.getConfirmPassword());
            persistedUser = userService.saveUser(user);

            if (persistedUser != null) {
                String message = persistedUser.getFirstName() + " user password changed by " + loggedUser.getFirstName() + " successfully";
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), message, "POST", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedUser, HttpStatus.CREATED);
    }
}