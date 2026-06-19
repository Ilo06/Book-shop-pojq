package com.example.demo.entity.keys;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArrivalBookIdTest {

  @Test
  void equalsAndHashCode() {
    UUID aId = UUID.randomUUID();
    UUID bId = UUID.randomUUID();
    ArrivalBookId id1 = new ArrivalBookId(aId, bId);
    ArrivalBookId id2 = new ArrivalBookId(aId, bId);
    ArrivalBookId id3 = new ArrivalBookId(UUID.randomUUID(), UUID.randomUUID());

    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertNotEquals(id1, id3);
    assertNotEquals(null, id1);
  }

  @Test
  void noArgsConstructor() {
    ArrivalBookId id = new ArrivalBookId();
    assertNotNull(id);
  }
}
