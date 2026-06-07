package com.example.demo.entity.keys;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleBookCopyId implements Serializable {
  private UUID saleId;
  private UUID bookCopyId;
}
