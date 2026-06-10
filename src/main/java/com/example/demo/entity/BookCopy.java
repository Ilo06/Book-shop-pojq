package com.example.demo.entity;

import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_copy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopy {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "book_status")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private BookStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "book_copy_type")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private BookCopyType type;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal price;

  @Column(length = 10, nullable = false)
  private String location;
}
