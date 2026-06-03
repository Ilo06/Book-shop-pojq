package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date saleDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "sale_books",
      joinColumns = @JoinColumn(name = "sale_id"),
      inverseJoinColumns = @JoinColumn(name = "book_id"))
  private List<Book> books;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal unitPrice;
}
