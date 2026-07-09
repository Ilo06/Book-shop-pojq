package com.example.demo.dto.response;

import com.example.demo.entity.enums.SaleStatus;
import java.time.Instant;
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
  private Instant creationDateTime;
  private SaleStatus saleStatus;
  private Instant finalizationDateTime;
  private Boolean isReservation;
  private List<SaleBookCopyResponse> books;
}
