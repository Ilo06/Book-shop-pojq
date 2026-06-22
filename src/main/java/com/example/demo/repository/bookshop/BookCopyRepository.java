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
    WITH total_arrival AS (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
                    FROM ArrivalBook AS ab
                    WHERE ab.arrivalBookId.bookCopyId = :copyId),
         total_sold AS (SELECT COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                    FROM SaleBookCopy AS sbc
                    WHERE sbc.bookCopy.id = :copyId)
    SELECT (arrival - sold)  AS copies FROM total_sold, total_arrival
    """
  )
  Integer findAvailableCopiesTypePerBookId(@Param("copyId") UUID copyId);

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
