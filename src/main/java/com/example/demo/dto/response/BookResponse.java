package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
  private UUID id;
  private String title;
  private String isbn;
  private String description;
  private BigDecimal price;
  private LocalDate publishDate;
  private GenreResponse genre;
  private List<AuthorResponse> authors;
}
