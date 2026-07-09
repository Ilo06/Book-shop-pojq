package com.example.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleDTO {
  @NotNull private Instant creationDateTime;
  @NotEmpty private List<QuantifiedBookCopyDTO> quantifiedBookCopyList;
  @NotNull private Boolean isReservation;
}
