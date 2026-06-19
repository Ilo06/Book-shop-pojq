package com.example.demo.entity.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReservationStatusTest {

  @Test
  void enumValues() {
    assertEquals(3, ReservationStatus.values().length);
    assertEquals(ReservationStatus.PENDING, ReservationStatus.valueOf("PENDING"));
    assertEquals(ReservationStatus.CONFIRMED, ReservationStatus.valueOf("CONFIRMED"));
    assertEquals(ReservationStatus.CANCELLED, ReservationStatus.valueOf("CANCELLED"));
  }
}
