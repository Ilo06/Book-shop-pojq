package com.example.demo.entity;

import com.example.demo.entity.keys.SaleBookCopyId;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sale_book_copy")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleBookCopy {
  @EmbeddedId private SaleBookCopyId saleBookCopyId;

  @ManyToOne
  @MapsId("saleId")
  @JoinColumn(name = "sale_id")
  private Sale sale;

  @OneToOne
  @MapsId("bookCopyId")
  @JoinColumn(name = "book_copy_id", unique = true)
  private BookCopy bookCopy;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal price;
}
