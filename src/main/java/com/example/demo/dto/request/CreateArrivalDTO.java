package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateArrivalDTO {
  @NotNull private Instant arrivalDateTime;
  @NotNull private List<QuantifiedBookCopyDTO> books;
}
