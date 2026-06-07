package com.example.demo.dto.response;

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
public class SaleResponse {
  private UUID id;
  private LocalDate saleDate;
  private List<SaleBookCopyResponse> books;
}
