package com.sda.caloriecounterbackend.mapper;

import com.sda.caloriecounterbackend.dto.UserDto;
import com.sda.caloriecounterbackend.entities.User;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapToDto(User user);
    User mapToDb(UserDto userDto);
}
