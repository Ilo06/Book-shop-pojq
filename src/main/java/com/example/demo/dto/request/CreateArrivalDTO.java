package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateArrivalDTO {
  private LocalDate arrivalDate;
  private List<CreateArrivalBookDTO> books;
}
