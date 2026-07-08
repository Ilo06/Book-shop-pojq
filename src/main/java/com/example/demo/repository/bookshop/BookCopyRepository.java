package com.example.demo.repository.bookshop;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.repository.projection.CopyStockProjection;
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

  @Query(
      """
  SELECT b.id            AS bookId,
         b.title         AS title,
         SUM(COALESCE(ta.arrival, 0) - COALESCE(ts.sold, 0)) AS availableCopies
  FROM Book b
  LEFT JOIN (SELECT sbc.bookCopy.book.id AS ts_bookId, COALESCE(SUM(sbc.quantity), 0) AS sold
          FROM SaleBookCopy AS sbc
          WHERE CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp
          GROUP BY sbc.bookCopy.book.id) ts ON ts.ts_bookId = b.id
  LEFT JOIN (SELECT ab.bookCopy.book.id AS ta_bookId, COALESCE(SUM(ab.quantity), 0) AS arrival
          FROM ArrivalBook AS ab
          WHERE CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp
          GROUP BY ab.bookCopy.book.id) ta ON ta.ta_bookId = b.id
  GROUP BY b.id, b.title
""")
  List<StockProjection> getAllBooksStock();

  @Query(
      """
SELECT (arrival - sold) AS copies
FROM
    (SELECT COALESCE(SUM(sbc.quantity), 0) AS sold
      FROM SaleBookCopy AS sbc
      WHERE sbc.bookCopy.book.id = :bookId AND CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp),
    (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
      FROM ArrivalBook AS ab
      JOIN ab.bookCopy AS bc
      WHERE bc.book.id = :bookId AND CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp)
""")
  Integer getBookStock(@Param("bookId") UUID bookId);

  @Query(
      """
SELECT COALESCE(ta.ta_type, ts.ts_type) AS type, SUM(COALESCE(ta.arrival, 0) - COALESCE(ts.sold, 0)) AS availableCopies
FROM (SELECT sbc.bookCopy.type AS ts_type, COALESCE(SUM(sbc.quantity), 0) AS sold
        FROM SaleBookCopy AS sbc
        WHERE sbc.bookCopy.book.id = :bookId AND CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp
        GROUP BY sbc.bookCopy.type) AS ts
FULL OUTER JOIN (SELECT ab.bookCopy.type AS ta_type, COALESCE(SUM(ab.quantity), 0) AS arrival
        FROM ArrivalBook AS ab
        WHERE ab.bookCopy.book.id = :bookId AND CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp
        GROUP BY ab.bookCopy.type) AS ta ON ts.ts_type = ta.ta_type
GROUP BY COALESCE(ta.ta_type, ts.ts_type)
""")
  List<CopyStockProjection> getDetailedBookStock(@Param("bookId") UUID bookId);

  @Query(
      """
SELECT (arrival - sold)  AS copies
FROM
    (SELECT COALESCE(SUM(sbc.quantity), 0) AS sold
        FROM SaleBookCopy AS sbc
        WHERE sbc.bookCopy.book.id = :bookId AND sbc.bookCopy.type = :copyType AND CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp),
    (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
        FROM ArrivalBook AS ab
        JOIN ab.bookCopy AS bc
        WHERE bc.book.id = :bookId AND bc.type = :copyType AND CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp)
""")
  Integer getBookCopyStockByType(
      @Param("bookId") UUID bookId, @Param("copyType") BookCopyType copyType);

  @Query(
      """
SELECT (arrival - sold) AS copies
FROM
    (SELECT COALESCE(SUM(sbc.quantity), 0) AS sold
        FROM SaleBookCopy AS sbc
        WHERE sbc.bookCopy.id = :copyId AND CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp),
    (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
        FROM ArrivalBook AS ab
        WHERE ab.bookCopy.id = :copyId AND CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp)
""")
  Integer getBookCopyStockByCopyId(@Param("copyId") UUID copyId);

  @Query(
      """
      SELECT b.id            AS bookId,
             b.title         AS title,
             SUM(COALESCE(ta.arrival, 0) - COALESCE(ts.sold, 0)) AS availableCopies
      FROM Book b
      LEFT JOIN (SELECT sbc.bookCopy.book.id AS ts_bookId, COALESCE(SUM(sbc.quantity), 0) AS sold
              FROM SaleBookCopy AS sbc
              WHERE CAST(sbc.sale.finalizationDateTime AS timestamp) <= current_timestamp
              GROUP BY sbc.bookCopy.book.id) ts ON ts.ts_bookId = b.id
      LEFT JOIN (SELECT ab.bookCopy.book.id AS ta_bookId, COALESCE(SUM(ab.quantity), 0) AS arrival
              FROM ArrivalBook AS ab
              WHERE CAST(ab.arrival.arrivalDateTime AS timestamp) <= current_timestamp
              GROUP BY ab.bookCopy.book.id) ta ON ta.ta_bookId = b.id
      GROUP BY b.id, b.title
      HAVING SUM(COALESCE(ta.arrival, 0) - COALESCE(ts.sold, 0)) <= 3
      """)
  List<StockProjection> findLowStockBooks();

  List<BookCopy> findAllByBookId(UUID bookId);
}
