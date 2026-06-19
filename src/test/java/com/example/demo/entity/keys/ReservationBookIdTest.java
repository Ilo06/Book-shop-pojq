package com.example.demo.entity.keys;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReservationBookIdTest {

  @Test
  void equalsAndHashCode() {
    UUID rId = UUID.randomUUID();
    UUID bId = UUID.randomUUID();
    ReservationBookId id1 = new ReservationBookId(rId, bId);
    ReservationBookId id2 = new ReservationBookId(rId, bId);
    ReservationBookId id3 = new ReservationBookId(UUID.randomUUID(), UUID.randomUUID());

    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertNotEquals(id1, id3);
  }

  @Test
  void noArgsConstructor() {
    ReservationBookId id = new ReservationBookId();
    assertNotNull(id);
  }
}
