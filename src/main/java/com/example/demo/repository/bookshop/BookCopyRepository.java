package com.example.demo.repository.bookshop;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.repository.projection.StockProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, UUID> {

  List<BookCopy> findByBookId(UUID bookId);

  List<BookCopy> findByStatus(BookStatus status);

  @Query(
      """
      SELECT b.id      AS bookId,
             b.title   AS title,
             COUNT(bc) AS availableCopies
      FROM BookCopy bc
      JOIN bc.book b
      WHERE bc.status = 'AVAILABLE'
      GROUP BY b.id, b.title
      """)
  List<StockProjection> findAvailableCopiesPerBook();

  @Query(
    """
    SELECT COUNT(bc) AS copies
    FROM BookCopy AS bc
    JOIN bc.book AS b
    WHERE b.id = :bookId
    """
  )
  Integer findAvailableCopiesPerBookId(@Param("bookId") UUID bookId);

  @Query(
          """
          SELECT COUNT(bc) AS copies
          FROM BookCopy AS bc
          JOIN bc.book AS b
          WHERE b.id = :bookId AND bc.type = LOWER(CAST(:bookType AS string))
          """
  )
  Integer findAvailableCopiesTypePerBookId(@Param("bookId") UUID bookId,
                              @Param("bookType") BookCopyType bookCopyType);

  @Query(
      """
      SELECT b.id      AS bookId,
             b.title   AS title,
             COUNT(bc) AS availableCopies
      FROM BookCopy bc
      JOIN bc.book b
      WHERE bc.status = 'AVAILABLE'
      GROUP BY b.id, b.title
      HAVING COUNT(bc) < 3
      """)
  List<StockProjection> findLowStockBooks();
}
