package com.sda.caloriecounterbackend.controller;

import com.sda.caloriecounterbackend.dto.ChangePasswordDto;
import com.sda.caloriecounterbackend.dto.DeleteAccountDto;
import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.exception.IncorrectPasswordException;
import com.sda.caloriecounterbackend.exception.UserNotFoundException;
import com.sda.caloriecounterbackend.exception.UsernameAlreadyExistException;
import com.sda.caloriecounterbackend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/v1")
@Log4j2
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto) {
        try {
            userService.register(userDto);
        } catch (UsernameAlreadyExistException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/registration/{token}")
    public ResponseEntity<?> confirmRegistration(@PathVariable(name = "token") String token) {
        try {
            userService.confirm(token);
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/current-user")
    public ResponseEntity<UserDto> getUserData(Principal principal) {
        UserDto userDto;
        try {
            userDto = userService.getUserByUsername(principal.getName());
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(userDto);
    }
    @PostMapping("/current-user")
    public ResponseEntity<?> deleteCurrentUser(Principal principal,
                                               @RequestBody @Valid DeleteAccountDto deleteAccountDto) {
        try {
            userService.delete(principal.getName(), deleteAccountDto.getPassword());
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IncorrectPasswordException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/current-user")
    public ResponseEntity<?> modifyUserData(Principal principal, @RequestBody @Valid UserDto userDto) {
        try {
            userService.modify(userDto, principal.getName());
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/change-password")
    public ResponseEntity<?> changeCurrentUserPassword(Principal principal,
                                                       @RequestBody @Valid ChangePasswordDto changePasswordDto) {
        try {
            userService.changePassword(changePasswordDto.getNewPassword(),
                    changePasswordDto.getOldPassword(), principal.getName());
        } catch (UserNotFoundException e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.noContent().build();
    }



}
