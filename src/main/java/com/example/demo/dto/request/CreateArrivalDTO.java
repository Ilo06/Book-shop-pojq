package com.example.demo.dto.request;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class CreateArrivalDTO {
  @NotNull private LocalDate arrivalDate;
  @NotNull private List<QuantifiedBookCopyDTO> books;
}
