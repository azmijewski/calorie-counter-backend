package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.dao.UserDao;
import com.sda.caloriecounterbackend.dto.MailDataDto;
import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;
import com.sda.caloriecounterbackend.exception.UserNotFoundException;
import com.sda.caloriecounterbackend.mapper.UserMapper;
import com.sda.caloriecounterbackend.service.impl.UserServiceImpl;
import com.sda.caloriecounterbackend.util.CustomMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserServiceImpl userService;
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(userDao, passwordEncoder,
                "%front-link%", "test", rabbitTemplate, userMapper);
    }


    @Test
    void shouldGetUserByUsername() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(userMapper.mapToDto(any())).thenReturn(new UserDto());
        //when
        ResponseEntity<UserDto> result = userService.getUserByUsername("test");
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }
    @Test
    void shouldNotGetUserByUsernameIfNotExist() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<UserDto> result = userService.getUserByUsername("test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void shouldFindById() {
        //given
        when(userDao.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(userMapper.mapToDto(any())).thenReturn(new UserDto());
        //when
        UserDto result = userService.findById(1L);
        //then
        assertNotNull(result);
    }
    @Test
    void shouldNotFindByIdIfNotExist() {
        //given
        when(userDao.findById(anyLong())).thenReturn(Optional.empty());
        //when && then
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }
    @Test
    void shouldFindAll() {
        //given
        when(userDao.getAll()).thenReturn(Collections.singletonList(new User()));
        when(userMapper.mapToDto(any())).thenReturn(new UserDto());
        //when
        List<UserDto> result = userService.findAll();
        //then
        assertFalse(result.isEmpty());
    }
    @Test
    void shouldModify() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        doNothing().when(userDao).modify(any());
        //when
        ResponseEntity<?> result = userService.modify(new UserDto(), "Test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotModifyIfNotExist() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userService.modify(new UserDto(), "Test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldDelete() {
        //given
        User user = new User();
        String password = "test";
        user.setPassword(password);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
        doNothing().when(userDao).delete(any());
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        //when
        ResponseEntity<?> result = userService.delete("test", password);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotDeleteIfNotExist() {
        //given
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userService.delete("test", "password");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldNotDeleteIfPasswordIsNotEquals() {
        //given
        User user = new User();
        String password = "test";
        String otherPassword = "other test";
        user.setPassword(password);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        //when
        ResponseEntity<?> result = userService.delete("test", otherPassword);
        //then
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }
    @Test
    void shouldRegister() {
        //given
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setEmail("user@user");
        when(userDao.existByLoginOrMail(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("test");
        when(userDao.save(any())).thenReturn(new User());
        when(userMapper.mapToDb(any())).thenReturn(new User());
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(MailDataDto.class));
        //when
        ResponseEntity<?> result = userService.register(userDto);
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void shouldNotRegisterIfExistByLoginOrMail() {
        //given
        UserDto userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setEmail("user@user");
        when(userDao.existByLoginOrMail(anyString(), anyString())).thenReturn(true);
        //when
        ResponseEntity<?> result = userService.register(userDto);
        //then
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }
    @Test
    void shouldConfirm() {
        //given
        when(userDao.findByToken(anyString())).thenReturn(Optional.of(new User()));
        doNothing().when(userDao).modify(any());
        //when
        ResponseEntity<?> result = userService.confirm("test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

    }
    @Test
    void shouldNotConfirmIfNotFound() {
        //given
        when(userDao.findByToken(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userService.confirm("test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldChangePassword() {
        //when
        User user = new User();
        String password = "test";
        user.setPassword(password);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
        doNothing().when(userDao).modify(any());
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("test");
        //when
        ResponseEntity<?> result = userService.changePassword("test", password, "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

    }
    @Test
    void shouldNotChangePasswordIfNotFound() {
        //when
        User user = new User();
        String password = "test";
        user.setPassword(password);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.empty());
        //when
        ResponseEntity<?> result = userService.changePassword("test", password, "test");
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void shouldNotChangePasswordIfPaaswordsNotEqual() {
        //when
        User user = new User();
        String password = "test";
        user.setPassword(password);
        when(userDao.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        //when
        ResponseEntity<?> result = userService.changePassword("test", password, "test");
        //then
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());

    }
}