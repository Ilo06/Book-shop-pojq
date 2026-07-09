package com.example.demo.dto.response;

import com.example.demo.entity.enums.BookCopyType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyStockResponse {
  private UUID bookId;
  private BookCopyType type;
  private Long availableCopies;
}
