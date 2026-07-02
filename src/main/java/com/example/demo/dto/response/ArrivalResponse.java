package com.example.demo.dto.response;

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
public class ArrivalResponse {
  private UUID id;
  private Instant arrivalDateTime;
  private List<ArrivalBookLine> books;
}
