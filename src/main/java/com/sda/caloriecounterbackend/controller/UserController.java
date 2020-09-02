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
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto) {
        return userService.register(userDto);
    }

    @PutMapping("/registration/{token}")
    public ResponseEntity<?> confirmRegistration(@PathVariable(name = "token") String token) {
        return userService.confirm(token);
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDto> getUserData(Principal principal) {
        return userService.getUserByUsername(principal.getName());
    }

    @PostMapping("/current-user")
    public ResponseEntity<?> deleteCurrentUser(Principal principal,
                                               @RequestBody @Valid DeleteAccountDto deleteAccountDto) {
        return userService.delete(principal.getName(), deleteAccountDto.getPassword());
    }

    @PutMapping("/current-user")
    public ResponseEntity<?> modifyUserData(Principal principal, @RequestBody @Valid UserDto userDto) {

        return userService.modify(userDto, principal.getName());
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changeCurrentUserPassword(Principal principal,
                                                       @RequestBody @Valid ChangePasswordDto changePasswordDto) {
        return userService.changePassword(changePasswordDto.getNewPassword(),
                changePasswordDto.getOldPassword(), principal.getName());
    }


}
