package com.works.etkinlikyonetimiuygulamasi.repository;

import com.works.etkinlikyonetimiuygulamasi.entity.Event;
import com.works.etkinlikyonetimiuygulamasi.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Sadece "PUBLISHED" (Yayında) olan etkinlikleri sayfalayarak getir
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    // Başlık veya kategoride arama yap (Sadece yayında olanlar içinde)
    @Query("SELECT e FROM Event e WHERE (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND e.status = :status")
    Page<Event> searchEvents(@Param("keyword") String keyword, @Param("status") EventStatus status, Pageable pageable);
}