package com.example.demo.dto.request;

import com.example.demo.entity.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchReservationDTO {
  @NotNull private ReservationStatus status;
}
