package com.example.demo.entity.keys;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SaleBookCopyIdTest {

  @Test
  void equalsAndHashCode() {
    UUID sId = UUID.randomUUID();
    UUID bId = UUID.randomUUID();
    SaleBookCopyId id1 = new SaleBookCopyId(sId, bId);
    SaleBookCopyId id2 = new SaleBookCopyId(sId, bId);
    SaleBookCopyId id3 = new SaleBookCopyId(UUID.randomUUID(), UUID.randomUUID());

    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertNotEquals(id1, id3);
  }

  @Test
  void noArgsConstructor() {
    SaleBookCopyId id = new SaleBookCopyId();
    assertNotNull(id);
    assertNull(id.getSaleId());
    assertNull(id.getBookCopyId());
  }

  @Test
  void gettersAndSetters() {
    UUID sId = UUID.randomUUID();
    UUID bId = UUID.randomUUID();
    SaleBookCopyId id = new SaleBookCopyId(sId, bId);
    assertEquals(sId, id.getSaleId());
    assertEquals(bId, id.getBookCopyId());

    id.setSaleId(null);
    id.setBookCopyId(null);
    assertNull(id.getSaleId());
    assertNull(id.getBookCopyId());
  }
}
