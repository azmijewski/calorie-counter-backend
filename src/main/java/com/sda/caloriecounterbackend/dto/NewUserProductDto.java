package com.sda.caloriecounterbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewUserProductDto {
    private Long productId;
    @Min(0)
    private Double weight;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
}
