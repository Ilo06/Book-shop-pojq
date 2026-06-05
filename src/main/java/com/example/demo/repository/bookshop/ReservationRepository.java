package com.example.demo.repository.bookshop;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.enums.ReservationStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

  List<Reservation> findByStatus(ReservationStatus status);
}
