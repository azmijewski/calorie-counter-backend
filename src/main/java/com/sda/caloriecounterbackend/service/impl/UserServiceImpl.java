package com.sda.caloriecounterbackend.service.impl;

import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dto.MailDataDto;
import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.exception.IncorrectPasswordException;
import com.sda.caloriecounterbackend.exception.UserNotFoundException;
import com.sda.caloriecounterbackend.mapper.UserMapper;
import com.sda.caloriecounterbackend.service.UserService;
import com.sda.caloriecounterbackend.util.CustomMailSender;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@PropertySource("classpath:message.properties")
public class UserServiceImpl implements UserService {

    private static final String FRONT_URL = "http://localhost:4200/confirmation/";
    private static final String FRONT_URL_REGEX = "%front-link%";

    private final UserDao userDao;
    private final CustomMailSender customMailSender;
    private final PasswordEncoder passwordEncoder;
    private final String welcomeMessage;
    private final String welcomeTopic;
    private final RabbitTemplate rabbitTemplate;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDao userDao, CustomMailSender customMailSender,
                           PasswordEncoder passwordEncoder,
                           @Value("${token-message}") String welcomeMessage,
                           @Value("${token-topic}") String welcomeTopic, RabbitTemplate rabbitTemplate,
                           UserMapper userMapper) {
        this.userDao = userDao;
        this.customMailSender = customMailSender;
        this.passwordEncoder = passwordEncoder;
        this.welcomeMessage = welcomeMessage;
        this.welcomeTopic = welcomeTopic;
        this.rabbitTemplate = rabbitTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserDto> getUserByUsername(String username) {
        log.info("Getting username");
        UserDto userDto;
        try {
            User user = findByUsername(username);
            userDto = userMapper.mapToDto(user);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(userDto);


    }

    @Override
    public UserDto findById(Long userId) {
        log.info("Find user by id: {}", userId);
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with id: " + userId));
        return userMapper.mapToDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("Find all users");
        return userDao.getAll().stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> modify(UserDto user, String username) {
        log.info("Modify user account");
        try {
            User userToModify = findByUsername(username);
            userToModify.setCalorie(user.getCalorie());
            userToModify.setFirstName(user.getFirstName());
            userToModify.setLastName(user.getLastName());
            userToModify.setEmail(user.getEmail());
            userToModify.setHeight(user.getHeight());
            userToModify.setWeight(user.getWeight());
            userDao.modify(userToModify);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<?> delete(String username, String password) {
        log.info("Delete user");
        try {
            User userToDelete = findByUsername(username);
            if (passwordEncoder.matches(password, userToDelete.getPassword())) {
                userDao.delete(userToDelete);
            } else {
                throw new IncorrectPasswordException();
            }
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IncorrectPasswordException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();

    }

    @Override
    public ResponseEntity<?> register(UserDto userDto) {
        log.info("Register new user");
        if(userDao.existByLoginOrMail(userDto.getUsername(), userDto.getEmail())){
            log.error("User with login: {}, or email: {} already exist", userDto.getUsername(), userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = userMapper.mapToDb(userDto);
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setIsConfirmed(false);
        String hashedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPass);
        userDao.save(user);
        MailDataDto mailDataDto = new MailDataDto();
        mailDataDto.setTo(user.getEmail());
        mailDataDto.setTopic(welcomeTopic);
        mailDataDto.setContent(prepareWelcomeMessage(token));
        this.rabbitTemplate.convertAndSend("mailQueue", mailDataDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> confirm(String token) {
        log.info("Confirm new user");
        try {
            User userToConfirmed = userDao.findByToken(token)
                    .orElseThrow(() -> new UserNotFoundException("Could not find user with token: " + token));
            userToConfirmed.setIsConfirmed(true);
            userToConfirmed.setToken(null);
            userDao.modify(userToConfirmed);
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> changePassword(String newPassword, String oldPassword, String username) {
        log.info("Change user password");
        try {
            User userToChangePassword = findByUsername(username);
            if (passwordEncoder.matches(oldPassword, userToChangePassword.getPassword())) {
                String hashedPassword = passwordEncoder.encode(newPassword);
                userToChangePassword.setPassword(hashedPassword);
                userDao.modify(userToChangePassword);
            } else {
                throw new IncorrectPasswordException();
            }
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IncorrectPasswordException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }

    @RabbitListener(queues = "mailQueue")
    public void registerUserMailSender(MailDataDto mailDataDto) {
        this.customMailSender.sendMail(mailDataDto.getTo(), mailDataDto.getTopic(), mailDataDto.getContent());
    }

    private User findByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
    }

    private String prepareWelcomeMessage(String token) {
        return welcomeMessage.replace(FRONT_URL_REGEX, FRONT_URL + token);
    }
}
