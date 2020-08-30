package com.sda.caloriecounterbackend.util;

import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.entities.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username: " + s));
        if (!user.getIsConfirmed()) {
            throw new UsernameNotFoundException("User with username: " + s + " is not confirmed");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}
