package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReservationDTO {
  private LocalDate date;
  private List<CreateArrivalBookDTO> books;
}
