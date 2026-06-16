package com.example.demo.dto.request;

import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookCopyDTO {

  @NotNull private UUID bookId;

  @NotNull private BookCopyType type;

  @NotNull
  @DecimalMin("0.0")
  private BigDecimal price;

  @Size(max = 100)
  private String location;

  private BookStatus status = BookStatus.AVAILABLE;
}
