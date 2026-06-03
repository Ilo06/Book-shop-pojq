package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSaleBookCopyDTO {
  private String saleId;
  private String bookCopyId;
  private double unitPrice;
  private int quantity;
}
