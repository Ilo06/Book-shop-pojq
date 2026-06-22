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

  // Had to cast everything to make it work
  @Query(
      """
      SELECT b FROM Book b
      WHERE (CAST(:genreId as uuid)  IS NULL OR b.genre.id = :genreId)
        AND (CAST(:authorId as uuid) IS NULL OR EXISTS (
              SELECT 1 FROM Author a JOIN a.books ab WHERE ab.id = b.id AND a.id = :authorId))
        AND (CAST(:search AS string)   IS NULL OR
              CAST(b.title AS string) ILIKE CAST(CONCAT('%', :search, '%') AS string ) OR
              CAST(b.isbn AS string)  ILIKE CAST(CONCAT('%', :search, '%') AS string))
      """)
  List<Book> findByFilters(
      @Param("genreId") UUID genreId,
      @Param("authorId") UUID authorId,
      @Param("search") String search);

  @Query(
    """
    WITH total_arrival AS (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
                    FROM ArrivalBook AS ab
                    JOIN BookCopy AS bc ON bc.id = ab.arrivalBookId.bookCopyId
                    WHERE bc.book.id = :bookId),
         total_sold AS (SELECT COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                    FROM SaleBookCopy AS sbc
                    WHERE sbc.bookCopy.book.id = :bookId)
    SELECT (arrival - sold)  AS copies FROM total_sold, total_arrival
    """
  )
  Integer findAvailableCopiesPerBookId(@Param("bookId") UUID bookId);
}
