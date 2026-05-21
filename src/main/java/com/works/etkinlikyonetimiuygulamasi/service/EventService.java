package com.works.etkinlikyonetimiuygulamasi.service;
import com.works.etkinlikyonetimiuygulamasi.dto.UserResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import com.works.etkinlikyonetimiuygulamasi.dto.EventCreateDto;
import com.works.etkinlikyonetimiuygulamasi.dto.EventUpdateDto;
import com.works.etkinlikyonetimiuygulamasi.dto.EventResponseDto;
import com.works.etkinlikyonetimiuygulamasi.entity.Event;
import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import com.works.etkinlikyonetimiuygulamasi.entity.Users;
import com.works.etkinlikyonetimiuygulamasi.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final HttpServletRequest request;

    public ResponseEntity createEvent(EventCreateDto eventCreateDto) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Oturum bulunamadi."));
        }

        Event event = modelMapper.map(eventCreateDto, Event.class);
        event.setOwner(currentUser);
        event.setStatus(EventStatus.PUBLISHED);

        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik basariyla olusturuldu",
                "event_id", event.getId()
        ));
    }

    public ResponseEntity updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Guncellenecek etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinligi duzenleme yetkiniz yok!"));
        }

        event.setTitle(eventUpdateDto.getTitle());
        event.setDate(eventUpdateDto.getDate());
        event.setTime(eventUpdateDto.getTime());
        event.setLocation(eventUpdateDto.getLocation());
        event.setDescription(eventUpdateDto.getDescription());
        event.setCategory(eventUpdateDto.getCategory());

        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik basariyla guncellendi.",
                "event_id", event.getId()
        ));
    }

    public ResponseEntity changeEventStatus(Long eventId, EventStatus newStatus) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinligin durumunu degistirme yetkiniz yok!"));
        }

        event.setStatus(newStatus);
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik durumu basariyla guncellendi.",
                "event_id", event.getId()
        ));
    }

    public ResponseEntity deleteEvent(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Silinecek etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinligi silme yetkiniz yok!"));
        }

        eventRepository.delete(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik basariyla silindi."
        ));
    }

    public ResponseEntity getAllPublishedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);

        Page<EventResponseDto> dtoPage = events.map(event -> {
            EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
            dto.setOwnerName(event.getOwner().getUsername());
            return dto;
        });

        return ResponseEntity.ok(dtoPage);
    }

    public ResponseEntity searchEvents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.searchEvents(keyword, EventStatus.PUBLISHED, pageable);

        Page<EventResponseDto> dtoPage = events.map(event -> {
            EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
            dto.setOwnerName(event.getOwner().getUsername());
            return dto;
        });

        return ResponseEntity.ok(dtoPage);
    }

    public ResponseEntity getEventDetail(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();
        EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
        dto.setOwnerName(event.getOwner().getUsername());

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity joinEvent(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();

        if (event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Kendi olusturdugunuz etkinlige katilmazsiniz."));
        }

        boolean isAlreadyJoined = event.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));

        if (isAlreadyJoined) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Bu etkinlige zaten katildiniz."));
        }

        event.getParticipants().add(currentUser);
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlige basariyla katildiniz!"
        ));
    }

    public ResponseEntity getEventParticipants(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadi."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Sadece etkinlik sahibi katilimcilari gorebilir."));
        }

        List<UserResponseDto> participantsList = event.getParticipants().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(participantsList);
    }

    public ResponseEntity getMyEvents() {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lutfen once giris yapin."));
        }

        List<Event> events = eventRepository.findByOwner(currentUser);
        List<EventResponseDto> dtos = events.stream()
                .map(e -> {
                    EventResponseDto dto = modelMapper.map(e, EventResponseDto.class);
                    dto.setOwnerName(e.getOwner().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(dtos);
    }
}