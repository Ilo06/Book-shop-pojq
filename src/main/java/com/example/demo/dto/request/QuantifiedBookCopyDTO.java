package com.example.demo.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class QuantifiedBookCopyDTO {
  @NotNull private UUID bookCopyId;
  @NotNull private Integer quantity;
}
