package com.example.demo.entity;

import com.example.demo.entity.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;
}