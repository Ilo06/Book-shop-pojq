package com.example.demo.entity;

import com.example.demo.entity.keys.ReservationBookId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBook {
  @EmbeddedId private ReservationBookId reservationBookId;

  @JsonIgnore
  @ManyToOne
  @MapsId("reservationId")
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  @ManyToOne
  @MapsId("bookCopyId")
  @JoinColumn(name = "book_copy_id")
  private BookCopy book;

  @Column(nullable = false)
  private int quantity;
}
