package com.example.demo.entity.keys;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class SaleBookCopyId implements Serializable {
  private UUID saleId;
  private UUID bookCopyId;
}
