/**
 *
 */
package com.dfq.coeffi.cbs.user.service;

import com.dfq.coeffi.cbs.user.entity.CustomUserDetails;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author H Kapil Kumar
 */

@Service("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        userOptional
                .orElseThrow(() -> new UsernameNotFoundException("user dose not exist " + email));
        return userOptional.map(user -> new CustomUserDetails(user)).get();
    }
}