package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateArrivalBookDTO {
    private UUID bookId;
    private int quantity;
}
