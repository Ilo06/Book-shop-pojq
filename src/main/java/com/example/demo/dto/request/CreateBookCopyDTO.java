package com.example.demo.dto.request;

import com.example.demo.entity.enums.BookCopyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookCopyDTO {
  @NotNull private BookCopyType type;

  @NotNull
  @DecimalMin("0.0")
  private BigDecimal price;

  @Size(max = 100)
  private String location;
}
