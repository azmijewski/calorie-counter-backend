package com.sda.caloriecounterbackend.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
@Component
public class DateMapper {
    public LocalDate mapToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }
    public Date mapToDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }
}
