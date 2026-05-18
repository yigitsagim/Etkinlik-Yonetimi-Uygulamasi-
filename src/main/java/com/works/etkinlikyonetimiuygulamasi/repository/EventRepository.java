package com.works.etkinlikyonetimiuygulamasi.repository;

import com.works.etkinlikyonetimiuygulamasi.entity.Event;
import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Status'e göre sayfalayarak getir (Örn: Sadece Yayında olanlar)
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    // Başlık veya Kategoriye göre arama yapma
    @Query("SELECT e FROM Event e WHERE (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND e.status = :status")
    Page<Event> searchEvents(String keyword, EventStatus status, Pageable pageable);
}