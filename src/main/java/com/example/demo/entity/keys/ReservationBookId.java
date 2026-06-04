package com.example.demo.entity.keys;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ReservationBookId implements Serializable {
  private UUID reservationId;
  private UUID bookId;
}
