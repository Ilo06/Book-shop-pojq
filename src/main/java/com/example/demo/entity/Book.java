package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(unique = true, nullable = false)
  private String isbn;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String description;

  private LocalDate publishDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "genre_id", nullable = false)
  private Genre genre;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "author_id"))
  private List<Author> authors;
}
