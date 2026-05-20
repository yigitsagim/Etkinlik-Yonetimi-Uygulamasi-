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

    // Etkinlik Oluşturma
    public ResponseEntity createEvent(EventCreateDto eventCreateDto) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Oturum bulunamadı."));
        }

        Event event = modelMapper.map(eventCreateDto, Event.class);
        event.setOwner(currentUser);
        event.setStatus(EventStatus.PUBLISHED);

        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik başarıyla oluşturuldu",
                "event_id", event.getId()
        ));
    }

    // Etkinlik Düzenleme (Güncelleme)
    public ResponseEntity updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Güncellenecek etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinliği düzenleme yetkiniz yok!"));
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
                "message", "Etkinlik başarıyla güncellendi.",
                "event_id", event.getId()
        ));
    }

    // Etkinlik Durumu (Status) Değiştirme
    public ResponseEntity changeEventStatus(Long eventId, EventStatus newStatus) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinliğin durumunu değiştirme yetkiniz yok!"));
        }

        event.setStatus(newStatus);
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik durumu başarıyla '" + newStatus.name() + "' olarak güncellendi.",
                "event_id", event.getId()
        ));
    }

    // Etkinlik Silme
    public ResponseEntity deleteEvent(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Silinecek etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();

        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinliği silme yetkiniz yok!"));
        }

        eventRepository.delete(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik başarıyla silindi."
        ));
    }

    // 1. Yayındaki Etkinlikleri Listele (Pagination)
    public ResponseEntity getAllPublishedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> events = eventRepository.findByStatus(EventStatus.PUBLISHED, pageable);

        // Entity listesini DTO listesine çevir
        Page<EventResponseDto> dtoPage = events.map(event -> {
            EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
            dto.setOwnerName(event.getOwner().getUsername());
            return dto;
        });

        return ResponseEntity.ok(dtoPage);
    }

    // 2. Etkinlik Ara
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

    // 3. Tekil Etkinlik Detayı
    public ResponseEntity getEventDetail(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();
        EventResponseDto dto = modelMapper.map(event, EventResponseDto.class);
        dto.setOwnerName(event.getOwner().getUsername());

        return ResponseEntity.ok(dto);
    }

    // 1. Etkinliğe Katılma
    public ResponseEntity joinEvent(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();

        // Kendi oluşturduğu etkinliğe katılmasını engellemek istersen:
        if (event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Kendi oluşturduğunuz etkinliğe katılamazsınız."));
        }

        // Daha önce katılmış mı kontrolü
        boolean isAlreadyJoined = event.getParticipants().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));

        if (isAlreadyJoined) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Bu etkinliğe zaten katıldınız."));
        }

        // Katılımcı listesine ekle ve kaydet
        event.getParticipants().add(currentUser);
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinliğe başarıyla katıldınız!"
        ));
    }

    // 2. Katılımcıları Listeleme (Sadece Etkinlik Sahibi Görebilir)
    public ResponseEntity getEventParticipants(Long eventId) {
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Etkinlik bulunamadı."));
        }

        Event event = optionalEvent.get();

        // GÜVENLİK: Katılımcıları sadece etkinliği oluşturan kişi görebilir
        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Sadece etkinlik sahibi katılımcıları görebilir."));
        }

        // Katılımcıları (Şifresiz olarak) DTO listesine çevir
        List<UserResponseDto> participantsList = event.getParticipants().stream().map(user -> {
            UserResponseDto dto = new UserResponseDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(participantsList);
    }
}