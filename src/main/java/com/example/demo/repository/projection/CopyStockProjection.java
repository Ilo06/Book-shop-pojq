package com.example.demo.repository.projection;

import com.example.demo.entity.enums.BookCopyType;

public interface CopyStockProjection {
  BookCopyType getType();

  Long getAvailableCopies();
}
