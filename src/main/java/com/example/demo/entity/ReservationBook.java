package com.example.demo.entity;

import com.example.demo.entity.keys.ReservationBookId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="reservation_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBook {
    @EmbeddedId
    private ReservationBookId reservationBookId;

    @ManyToOne
    @MapsId("reservationId")
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private int quantity;

}
