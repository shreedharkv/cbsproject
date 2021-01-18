package com.dfq.coeffi.cbs.user.dto;

import com.dfq.coeffi.cbs.user.entity.Role;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class UserConverter {

    @Autowired
    private UserService userService;

    public User toEntity(UserDto resource) {
        User user = new User();

        if(resource.getRoleId() != 0){
            setRoles(user,resource);
        }
        user.setEmail(resource.getEmail());
        user.setPassword(resource.getPassword());
        user.setUserCode(resource.getUserCode());
        user.setActive(true);
        user.setFirstName(resource.getFirstName());
        user.setMiddleName(resource.getMiddleName());
        user.setLastName(resource.getLastName());
        user.setGender(resource.getGender());
        user.setJobTitle(resource.getJobTitle());
        user.setDateOfJoining(resource.getDateOfJoining());
        user.setOfferedSalary(resource.getOfferedSalary());
        user.setPersonnelDetails(resource.getPersonnelDetails());
        user.setContactInformation(resource.getContactInformation());
        user.setUserAddress(resource.getUserAddress());
        return user;
    }

    private void setRoles(User user, UserDto resource){
        Role role = userService.getRole(resource.getRoleId());
        user.setRoles(Arrays.asList(role));
    }
}