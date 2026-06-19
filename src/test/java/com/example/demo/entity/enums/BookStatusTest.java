package com.example.demo.entity.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookStatusTest {

  @Test
  void enumValues() {
    assertEquals(2, BookStatus.values().length);
    assertEquals(BookStatus.AVAILABLE, BookStatus.valueOf("AVAILABLE"));
    assertEquals(BookStatus.SOLD_OUT, BookStatus.valueOf("SOLD_OUT"));
  }
}
