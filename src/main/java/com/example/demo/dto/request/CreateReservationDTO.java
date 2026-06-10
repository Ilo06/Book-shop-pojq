package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReservationDTO {
  @NotNull private LocalDate date;
  @NotNull private List<QuantifiedBookCopyDTO> books;
}
