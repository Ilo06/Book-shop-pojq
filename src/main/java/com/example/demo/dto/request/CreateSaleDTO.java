package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleDTO {
  @NotNull private LocalDate saleDate;
  @NotNull private List<CreateSaleBookCopyDTO> books;
}
