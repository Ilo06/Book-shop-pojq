package com.example.demo.dto.response;

import java.time.Year;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ISBNSearchResponse {
  private String title;
  private String isbn;
  private Year firstPublishYear;
  private List<String> authors;
}
