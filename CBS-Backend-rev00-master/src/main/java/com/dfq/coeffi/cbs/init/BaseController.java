package com.dfq.coeffi.cbs.init;

import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @Auther H Kapil Kumar
 * @Company Orileo Technologies
 */
@RestController
@RequestMapping("/api/v1")
public class BaseController {

    @Autowired
    private UserService userService;

    public User getLoggedUser(Principal principal) {
        User user = userService.getUser(Integer.parseInt(principal.getName()));
   
        return user;
    }
}