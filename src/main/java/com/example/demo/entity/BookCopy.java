package com.example.demo.entity;

import com.example.demo.entity.enums.BookCopyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "book_copy_type")
  private BookCopyType type;

  @JsonIgnore
  @OneToMany(mappedBy = "bookCopy")
  private List<BookCopyPrice> prices;

  @Column(length = 10, nullable = false)
  private String location;

  @JsonIgnore
  @Transient
  public BigDecimal getPrice() {
    return prices.stream()
            .max(Comparator.comparing(BookCopyPrice::getDate))
            .orElse(new BookCopyPrice(null, null ,null , BigDecimal.valueOf(0.0F)))
            .getPrice();
  }

  @JsonIgnore
  @Transient
  public BigDecimal getPrice(Instant date) {
    return prices.stream()
            .filter(p -> p.getDate().isBefore(date))
            .max(Comparator.comparing(BookCopyPrice::getDate))
            .orElse(new BookCopyPrice(null, null ,null , BigDecimal.valueOf(0.0F)))
            .getPrice();
  }
}
