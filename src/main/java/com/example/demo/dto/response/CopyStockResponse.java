package com.example.demo.dto.response;

import com.example.demo.entity.enums.BookCopyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyStockResponse {
    private UUID bookId;
    private BookCopyType type;
    private Long availableCopies;
}