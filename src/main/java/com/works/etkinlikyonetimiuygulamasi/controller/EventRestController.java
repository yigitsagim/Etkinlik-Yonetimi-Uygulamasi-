package com.works.etkinlikyonetimiuygulamasi.controller;

import com.works.etkinlikyonetimiuygulamasi.dto.EventCreateDto;
import com.works.etkinlikyonetimiuygulamasi.dto.EventUpdateDto;
import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import com.works.etkinlikyonetimiuygulamasi.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService; //

    @PostMapping("/create")
    public ResponseEntity createEvent(@Valid @RequestBody EventCreateDto eventCreateDto) {
        return eventService.createEvent(eventCreateDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        return eventService.updateEvent(id, eventUpdateDto);
    }
    // Etkinliği Yayınla
    @PatchMapping("/{id}/publish")
    public ResponseEntity publishEvent(@PathVariable Long id) {
        return eventService.changeEventStatus(id, EventStatus.PUBLISHED);
    }

    // Etkinlik Yayınını Durdur
    @PatchMapping("/{id}/pause")
    public ResponseEntity pauseEvent(@PathVariable Long id) {
        return eventService.changeEventStatus(id, EventStatus.PAUSED);
    }

    // Etkinliği Arşivle
    @PatchMapping("/{id}/archive")
    public ResponseEntity archiveEvent(@PathVariable Long id) {
        return eventService.changeEventStatus(id, EventStatus.ARCHIVED);
    }
    // Etkinliği Sil
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }
    // Tüm Yayındaki Etkinlikleri Listele (Sayfalama ile)
    // Örnek kullanım: /api/events?page=0&size=10
    @GetMapping
    public ResponseEntity getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllPublishedEvents(page, size);
    }

    // Etkinlik Ara
    // Örnek kullanım: /api/events/search?keyword=konser&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity searchEvents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.searchEvents(keyword, page, size);
    }

    // Etkinlik Detayı Getir
    @GetMapping("/{id}")
    public ResponseEntity getEventDetail(@PathVariable Long id) {
        return eventService.getEventDetail(id);
    }
    // Etkinliğe Katıl
    @PostMapping("/{id}/join")
    public ResponseEntity joinEvent(@PathVariable Long id) {
        return eventService.joinEvent(id);
    }

    // Etkinliğin Katılımcılarını Listele (Sadece Kurucu)
    @GetMapping("/{id}/participants")
    public ResponseEntity getEventParticipants(@PathVariable Long id) {
        return eventService.getEventParticipants(id);
    }
}