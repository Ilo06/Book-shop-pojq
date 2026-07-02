package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "book_copy_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCopyPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_copy_id",  nullable = false, referencedColumnName = "id")
    private BookCopy bookCopy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @Builder.Default
    private Instant date = Instant.now();

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
}
