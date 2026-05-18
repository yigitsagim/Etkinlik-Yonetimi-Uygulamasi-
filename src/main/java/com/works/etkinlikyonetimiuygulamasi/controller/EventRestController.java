package com.works.etkinlikyonetimiuygulamasi.controller;

import com.works.etkinlikyonetimiuygulamasi.dto.EventCreateDto;
import com.works.etkinlikyonetimiuygulamasi.dto.EventUpdateDto; // <-- EKSİK OLAN IMPORT EKLENDİ
import com.works.etkinlikyonetimiuygulamasi.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService; // <-- NOKTALI VİRGÜL EKLENDİ

    @PostMapping("/create")
    public ResponseEntity createEvent(@Valid @RequestBody EventCreateDto eventCreateDto) {
        return eventService.createEvent(eventCreateDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        return eventService.updateEvent(id, eventUpdateDto);
    }
}