package com.example.demo.repository.bookshop;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.enums.ReservationStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

  Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
