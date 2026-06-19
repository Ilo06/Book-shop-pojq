package com.example.demo.entity.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BookCopyTypeTest {

  @Test
  void enumValues() {
    assertEquals(3, BookCopyType.values().length);
    assertEquals(BookCopyType.HARDBACK, BookCopyType.valueOf("HARDBACK"));
    assertEquals(BookCopyType.PAPERBACK, BookCopyType.valueOf("PAPERBACK"));
    assertEquals(BookCopyType.POCKET, BookCopyType.valueOf("POCKET"));
  }
}
