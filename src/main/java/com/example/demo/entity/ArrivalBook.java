package com.example.demo.entity;

import com.example.demo.entity.keys.ArrivalBookId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "arrival_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalBook {
  @EmbeddedId private ArrivalBookId arrivalBookId;

  @JsonIgnore
  @ManyToOne
  @MapsId("arrivalId")
  @JoinColumn(name = "arrival_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Arrival arrival;

  @ManyToOne
  @MapsId("bookCopyId")
  @JoinColumn(name = "book_copy_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private BookCopy book;

  @Column(nullable = false)
  private int quantity;
}
