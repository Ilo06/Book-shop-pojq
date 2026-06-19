package com.example.demo.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalBookLine {
  private UUID bookCopyId;
  private int quantity;
}
