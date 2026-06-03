package com.example.demo.dto.request;

import com.example.demo.entity.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBookCopyDTO {
  private String bookId;
  private String location;
  private BookStatus status;
}
