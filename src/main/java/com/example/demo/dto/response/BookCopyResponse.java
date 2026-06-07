package com.example.demo.dto.response;

import com.example.demo.entity.enums.BookStatus;
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
  private BookStatus status;
  private String location;
}
