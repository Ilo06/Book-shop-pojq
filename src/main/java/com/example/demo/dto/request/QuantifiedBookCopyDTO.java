package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuantifiedBookCopyDTO {
  @NotNull private UUID bookCopyId;
  @NotNull private Integer quantity;
}
