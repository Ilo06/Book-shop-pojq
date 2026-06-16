package com.example.demo.dto.response;

import com.example.demo.entity.enums.ReservationStatus;
import java.time.LocalDateTime;
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
public class ReservationResponse {
  private UUID id;
  private LocalDateTime reservationDate;
  private ReservationStatus status;
  private List<ReservationBookResponse> books;
}
