package com.example.demo.dto.response;

import com.example.demo.entity.enums.BookCopyType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResponse {
  private UUID id;
  private UUID bookId;
  private BookCopyType type;
  private BigDecimal price;
  private String location;
}
