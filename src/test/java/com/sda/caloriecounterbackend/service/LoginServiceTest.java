package com.sda.caloriecounterbackend.service;

import com.sda.caloriecounterbackend.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @InjectMocks
    private LoginServiceImpl loginService;
    @Mock
    AuthenticationManager authenticationManager;

    @Test
    void shouldLoginIfExist() {
        //given
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("test", "test"));
        //when
        ResponseEntity<?> result = loginService.login("test", "test");
        //then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
    @Test
    void shouldNotLoginIfNotExist() {
        //given
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        //when
        ResponseEntity<?> result = loginService.login("test", "test");
        //then
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

}