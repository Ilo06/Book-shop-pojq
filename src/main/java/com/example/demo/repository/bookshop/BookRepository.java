package com.example.demo.repository.bookshop;

import com.example.demo.entity.Book;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

  boolean existsByIsbn(String isbn);

  @Query(
      """
      SELECT b FROM Book b
      WHERE (:genreFlag = false OR b.genre.id = :genreId)
        AND (:authorFlag = false OR EXISTS (
              SELECT 1 FROM b.authors a WHERE a.id = :authorId))
      AND (:searchFlag = false OR LOWER(b.title) LIKE :pattern OR LOWER(b.isbn) LIKE :pattern)
      """)
  List<Book> findByFilters(
      @Param("genreId") UUID genreId,
      @Param("authorId") UUID authorId,
      @Param("pattern") String pattern,
      @Param("genreFlag") boolean genreFlag,
      @Param("authorFlag") boolean authorFlag,
      @Param("searchFlag") boolean searchFlag);
}
