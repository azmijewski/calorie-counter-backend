package com.sda.caloriecounterbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mapstruct.AfterMapping;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeleteAccountDto implements Serializable {
    @NotBlank
    private String password;
}
