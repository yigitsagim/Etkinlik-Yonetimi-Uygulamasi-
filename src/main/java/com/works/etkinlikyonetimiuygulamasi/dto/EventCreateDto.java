package com.works.etkinlikyonetimiuygulamasi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for {@link com.works.etkinlikyonetimiuygulamasi.entity.Event}
 */
@Value
public class EventCreateDto implements Serializable {
    @NotBlank
    String title;
    @FutureOrPresent
    LocalDate date;
    @NotNull
    LocalTime time;
    @NotBlank
    String location;
    String description;
    @NotBlank
    String category;
}