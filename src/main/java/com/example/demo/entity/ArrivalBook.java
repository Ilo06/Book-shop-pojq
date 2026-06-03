package com.example.demo.entity;

import com.example.demo.entity.keys.ArrivalBookId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="arrival_book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalBook {
    @EmbeddedId
    private ArrivalBookId arrivalBookId;

    @ManyToOne
    @MapsId("arrivalId")
    @JoinColumn(name = "arrival_id")
    private Arrival arrival;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private int quantity;

}
