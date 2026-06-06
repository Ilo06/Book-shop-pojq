package com.example.demo.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface RevenueByGenreProjection {
  UUID getGenreId();
  String getGenreName();
  BigDecimal getTotalRevenue();
}
