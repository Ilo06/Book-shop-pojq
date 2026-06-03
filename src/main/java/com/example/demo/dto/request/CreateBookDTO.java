package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBookDTO {
  private String title;
  private String isbn;
  private String description;
  private double price;
  private LocalDate publishDate;
  private UUID genreId;
  private List<UUID> authorIds;
}
