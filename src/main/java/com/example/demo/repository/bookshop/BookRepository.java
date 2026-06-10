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
      WHERE (:genreId  IS NULL OR b.genre.id = :genreId)
        AND (:authorId IS NULL OR EXISTS (
              SELECT 1 FROM b.authors a WHERE a.id = :authorId))
        AND (:search   IS NULL OR
              CAST(b.title AS string) ILIKE CAST(CONCAT('%', :search, '%') AS string ) OR
              CAST(b.isbn AS string)  ILIKE CAST(CONCAT('%', :search, '%') AS string))
      """)
  List<Book> findByFilters( // This won't work, i cant figure out why
      @Param("genreId") UUID genreId,
      @Param("authorId") UUID authorId,
      @Param("search") String search);
}
