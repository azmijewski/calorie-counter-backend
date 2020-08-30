package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.exception.IncorrectPasswordException;
import com.sda.caloriecounterbackend.service.UserService;
import com.sda.caloriecounterbackend.util.CustomMailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@PropertySource("classpath:message.properties")
public class UserServiceImpl implements UserService {

    private static final String FRONT_URL = "http://localhost:4200/token/";
    private static final String FRONT_URL_REGEX = "%front-link%";

    private final UserDao userDao;
    private final CustomMailSender customMailSender;
    private final PasswordEncoder passwordEncoder;
    private final String welcomeMessage;
    private final String welcomeTopic;

    public UserServiceImpl(UserDao userDao, CustomMailSender customMailSender,
                           PasswordEncoder passwordEncoder,
                           @Value("${token-message}") String welcomeMessage,
                           @Value("${token-topic}") String welcomeTopic) {
        this.userDao = userDao;
        this.customMailSender = customMailSender;
        this.passwordEncoder = passwordEncoder;
        this.welcomeMessage = welcomeMessage;
        this.welcomeTopic = welcomeTopic;
    }

    @Override
    public User findById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with id: " + userId));
    }

    @Override
    public List<User> findAll() {
        return userDao.getAll();
    }

    @Override
    public void modify(UserDto user, String username) {
        User userToModify = findByUsername(username);
        userToModify.setCalorie(user.getCalorie());
        userToModify.setFirstName(user.getFirstName());
        userToModify.setLastName(user.getLastName());
        userToModify.setEmail(user.getEmail());
        userToModify.setHeight(user.getHeight());
        userToModify.setWeight(user.getWeight());
        userDao.modify(userToModify);
    }

    @Override
    public void delete(String username, String password) {
        User userToDelete = findByUsername(username);
        if (passwordEncoder.matches(password, userToDelete.getPassword())) {
            userDao.delete(userToDelete);
        } else {
            throw new IncorrectPasswordException();
        }

    }

    @Override
    public void register(User user) {
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setIsConfirmed(false);
        String hashedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPass);
        userDao.save(user);
        //customMailSender.sendMail(user.getEmail(), welcomeTopic, prepareWelcomeMessage(token));
    }

    @Override
    public void confirm(String token) {

    }

    @Override
    public void changePassword(String newPassword, String oldPassword,  String username) {
        User userToChangePassword = findByUsername(username);
        if (passwordEncoder.matches(oldPassword, userToChangePassword.getPassword())) {
            String hashedPassword = passwordEncoder.encode(newPassword);
            userToChangePassword.setPassword(hashedPassword);
            userDao.save(userToChangePassword);
        } else {
            throw new IncorrectPasswordException();
        }
    }

    private User findByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with username: " + username));
    }
    private String prepareWelcomeMessage(String token) {
        return welcomeMessage.replace(FRONT_URL_REGEX, FRONT_URL + token);
    }
}
