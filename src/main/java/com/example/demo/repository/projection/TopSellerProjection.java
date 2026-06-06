package com.example.demo.repository.projection;

import java.util.UUID;

public interface TopSellerProjection {
  UUID getBookId();
  String getTitle();
  Long getTotalQuantity();
}
