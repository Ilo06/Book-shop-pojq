package com.example.demo.entity;

import com.example.demo.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "reservation")
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
  @Column(nullable = false)
  private LocalDateTime reservationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "reservation_status")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED

  @OneToMany(mappedBy = "reservation")
  private List<ReservationBook> books;
}
