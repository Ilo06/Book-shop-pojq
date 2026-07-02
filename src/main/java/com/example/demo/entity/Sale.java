package com.example.demo.entity;

import com.example.demo.entity.enums.SaleStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private SaleStatus status = SaleStatus.PENDING;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isReservation = Boolean.FALSE;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  @Builder.Default
  private Instant creationDateTime = Instant.now();

  @Temporal(TemporalType.TIMESTAMP)
  @Column
  private Instant finalizationDateTime;

  @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY)
  @Builder.Default
  private List<SaleBookCopy> books = new ArrayList<>();
}
