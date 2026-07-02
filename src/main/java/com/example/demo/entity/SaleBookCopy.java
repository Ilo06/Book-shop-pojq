package com.example.demo.entity;

import com.example.demo.entity.keys.SaleBookCopyId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sale_book_copy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleBookCopy {
  @EmbeddedId private SaleBookCopyId saleBookCopyId;

  @JsonIgnore
  @ManyToOne
  @MapsId("saleId")
  @JoinColumn(name = "sale_id")
  private Sale sale;

  @OneToOne
  @MapsId("bookCopyId")
  @JoinColumn(name = "book_copy_id", unique = true)
  private BookCopy bookCopy;

  @Column(name = "quantity", nullable = false, columnDefinition = "INTEGER CHECK (quantity > 0)")
  private Integer quantity;
}
