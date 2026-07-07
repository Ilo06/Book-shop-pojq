package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "arrival_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalBook {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "arrival_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Arrival arrival;

  @ManyToOne
  @JoinColumn(name = "book_copy_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private BookCopy bookCopy;

  @Column(nullable = false)
  private int quantity;
}
