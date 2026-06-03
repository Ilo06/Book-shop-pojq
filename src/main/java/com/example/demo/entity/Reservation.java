package com.example.demo.entity;

import com.example.demo.entity.Book;
import com.example.demo.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date reservationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "reservation_books",
      joinColumns = @JoinColumn(name = "reservation_id"),
      inverseJoinColumns = @JoinColumn(name = "book_id"))
  private List<Book> books;
}
