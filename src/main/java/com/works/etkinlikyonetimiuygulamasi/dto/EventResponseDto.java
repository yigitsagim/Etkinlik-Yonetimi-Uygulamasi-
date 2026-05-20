package com.works.etkinlikyonetimiuygulamasi.dto;

import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventResponseDto {
    private Long id;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;
    private String category;
    private EventStatus status;
    // Etkinliği oluşturanın sadece adını dönüyoruz, şifresini veya emailini değil!
    private String ownerName;
}