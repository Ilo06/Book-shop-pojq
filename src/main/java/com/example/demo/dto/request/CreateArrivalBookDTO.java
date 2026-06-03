package com.example.demo.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateArrivalBookDTO {
  private UUID bookId;
  private int quantity;
}
