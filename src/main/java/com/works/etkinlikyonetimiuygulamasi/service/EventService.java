package com.works.etkinlikyonetimiuygulamasi.service;

import com.works.etkinlikyonetimiuygulamasi.dto.EventCreateDto;
import com.works.etkinlikyonetimiuygulamasi.dto.EventUpdateDto;
import com.works.etkinlikyonetimiuygulamasi.entity.Event;
import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import com.works.etkinlikyonetimiuygulamasi.entity.Users;
import com.works.etkinlikyonetimiuygulamasi.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional; // Eklendi

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final HttpServletRequest request;

    // Etkinlik Oluşturma
    public ResponseEntity createEvent(EventCreateDto eventCreateDto) {

        // 1. Session'dan giriş yapmış kullanıcıyı al
        Users currentUser = (Users) request.getSession().getAttribute("user");

        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Oturum bulunamadı."));
        }

        // 2. DTO'yu Entity'ye çevir
        Event event = modelMapper.map(eventCreateDto, Event.class);

        // 3. Etkinlik sahibini ve varsayılan durumu ayarla
        event.setOwner(currentUser);
        event.setStatus(EventStatus.PUBLISHED);

        // 4. Veritabanına kaydet
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik başarıyla oluşturuldu",
                "event_id", event.getId()
        ));
    } // <-- createEvent metodu burada doğru şekilde kapatıldı

    // Etkinlik Düzenleme (Güncelleme)
    public ResponseEntity updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {

        // 1. Session'dan anlık giriş yapmış kullanıcıyı al
        Users currentUser = (Users) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Lütfen önce giriş yapın."));
        }

        // 2. Güncellenmek istenen etkinliği veritabanından bul
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Güncellenecek etkinlik bulunamadı."));
        } // <-- Eksik olan süslü parantez eklendi

        Event event = optionalEvent.get();

        // 3. GÜVENLİK KONTROLÜ: Bu etkinliği değiştirmek isteyen kişi, etkinliğin sahibi mi?
        if (!event.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bu etkinliği düzenleme yetkiniz yok!"));
        }

        // 4. Verileri Güncelle (Küçük harfli nesne referansları kullanıldı)
        event.setTitle(eventUpdateDto.getTitle());
        event.setDate(eventUpdateDto.getDate());
        event.setTime(eventUpdateDto.getTime());
        event.setLocation(eventUpdateDto.getLocation());
        event.setDescription(eventUpdateDto.getDescription());
        event.setCategory(eventUpdateDto.getCategory());

        // 5. Veritabanına kaydet
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Etkinlik başarıyla güncellendi.",
                "event_id", event.getId()
        ));
    }
}