package com.example.demo.repository.bookshop;

import com.example.demo.entity.ReservationBook;
import com.example.demo.entity.keys.ReservationBookId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationBookRepository
    extends JpaRepository<ReservationBook, ReservationBookId> {

  List<ReservationBook> findByReservationId(UUID reservationId);
}
