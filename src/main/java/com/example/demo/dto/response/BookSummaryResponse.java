package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryResponse {
  private UUID id;
  private String title;
  private String isbn;
  private BigDecimal price;
  private GenreResponse genre;
}
