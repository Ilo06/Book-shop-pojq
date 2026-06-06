package com.example.demo.repository.projection;

import java.util.UUID;

public interface StockProjection {
  UUID getBookId();
  String getTitle();
  Long getAvailableCopies();
}
