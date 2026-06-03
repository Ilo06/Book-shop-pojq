package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "arrivals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arrival {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date arrivalDate;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "arrival_books",
      joinColumns = @JoinColumn(name = "arrival_id"),
      inverseJoinColumns = @JoinColumn(name = "book_id"))
  private List<Book> books;
}
