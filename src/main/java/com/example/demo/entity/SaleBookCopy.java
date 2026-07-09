package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sale_book_copy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleBookCopy {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "sale_id")
  private Sale sale;

  @ManyToOne
  @JoinColumn(name = "book_copy_id")
  private BookCopy bookCopy;

  @Column(name = "quantity", nullable = false, columnDefinition = "INTEGER CHECK (quantity > 0)")
  private Integer quantity;
}
