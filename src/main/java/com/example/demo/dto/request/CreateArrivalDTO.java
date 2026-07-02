package com.example.demo.dto.request;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class CreateArrivalDTO {
  @NotNull private Instant arrivalDateTime;
  @NotNull private List<QuantifiedBookCopyDTO> books;
}
