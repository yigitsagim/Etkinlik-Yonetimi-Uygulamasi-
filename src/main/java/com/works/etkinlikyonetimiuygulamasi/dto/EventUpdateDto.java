package com.works.etkinlikyonetimiuygulamasi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventUpdateDto {

    @NotBlank(message = "Etkinlik adı zorunludur")
    private String title;

    @NotNull(message = "Tarih zorunludur")
    @FutureOrPresent(message = "Geçmiş bir tarih seçilemez")
    private LocalDate date;

    @NotNull(message = "Saat zorunludur")
    private LocalTime time;

    @NotBlank(message = "Yer zorunludur")
    private String location;

    private String description;

    @NotBlank(message = "Kategori zorunludur")
    private String category;
}