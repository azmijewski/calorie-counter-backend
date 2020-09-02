package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDto implements Serializable {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
