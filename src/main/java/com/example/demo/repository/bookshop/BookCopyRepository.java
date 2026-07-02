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
             (ta.arrival - ts.sold) AS availableCopies
      FROM BookCopy bc
      JOIN bc.book b
      JOIN (SELECT ts_bc.book.id AS ts_book_id, COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
              FROM BookCopy as ts_bc
              JOIN SaleBookCopy AS sbc
              GROUP BY ts_bc.book.id) ts ON ts.ts_book_id = b.id
      JOIN (SELECT ta_bc.book.id AS ta_book_id, COALESCE(SUM(ab.quantity), 0) AS arrival
              FROM BookCopy as ta_bc
              JOIN ArrivalBook AS ab
              GROUP BY ta_bc.book.id) ta ON ta.ta_book_id = b.id
      GROUP BY b.id, b.title
    """
  )
  List<StockProjection> getAllBooksStock();

  @Query(
          """
          SELECT (arrival - sold) AS copies
          FROM
              (SELECT COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                FROM SaleBookCopy AS sbc
                WHERE sbc.bookCopy.book.id = :bookId),
              (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
                FROM ArrivalBook AS ab
                JOIN BookCopy AS bc ON bc.id = ab.arrivalBookId.bookCopyId
                WHERE bc.book.id = :bookId)
          """
  )
  Integer getBookStock(@Param("bookId") UUID bookId);

  @Query(
          """
          SELECT ta.ta_type AS type, (ta.arrival - ts.sold) AS availableCopies
          FROM (SELECT ts_bc.type AS ts_type, COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                  FROM SaleBookCopy AS sbc
                  JOIN BookCopy AS ts_bc ON sbc.bookCopy.book.id = ts_bc.book.id
                  WHERE ts_bc.book.id = :bookId) AS ts
          JOIN (SELECT ta_bc.type AS ta_type, COALESCE(SUM(ab.quantity), 0) AS arrival
                  FROM ArrivalBook AS ab
                  JOIN BookCopy AS ta_bc ON ab.book.book.id = ta_bc.book.id
                  WHERE ta_bc.book.id = :bookId) AS ta ON ts.ts_type = ta.ta_type
          GROUP BY ta.ta_type
          """
  )
  List<CopyStockProjection> getDetailedBookStock(@Param("bookId") UUID bookId);

  @Query(
    """
    SELECT (arrival - sold)  AS copies
    FROM
        (SELECT COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
            FROM SaleBookCopy AS sbc
            WHERE sbc.bookCopy.book.id = :bookId AND sbc.bookCopy.type = :copyType),
        (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
            FROM ArrivalBook AS ab
            JOIN BookCopy AS bc
            WHERE bc.book.id = :bookId AND bc.type = :copyType)
    """
  )
  Integer getBookCopyStockByType(@Param("bookId") UUID bookId,
                                 @Param("copyType") BookCopyType copyType);

  @Query(
          """
          SELECT (arrival - sold) AS copies
          FROM
              (SELECT COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                  FROM SaleBookCopy AS sbc
                  WHERE sbc.bookCopy.id = :copyId),
              (SELECT COALESCE(SUM(ab.quantity), 0) AS arrival
                  FROM ArrivalBook AS ab
                  WHERE ab.arrivalBookId.bookCopyId = :bookId )
          """
  )
  Integer getBookCopyStockByCopyId(@Param("copyId") UUID copyId);

  @Query(
          """
          SELECT b.id            AS bookId,
                 b.title         AS title,
                 (ta.arrival - ts.sold) AS availableCopies
          FROM BookCopy bc
          JOIN bc.book b
          JOIN (SELECT ts_bc.book.id AS ts_bookId, COALESCE(COUNT(sbc.bookCopy.id), 0) AS sold
                  FROM BookCopy as ts_bc
                  JOIN SaleBookCopy AS sbc
                  GROUP BY ts_bc.book.id) ts ON ts.ts_bookId = b.id
          JOIN (SELECT ta_bc.book.id AS ta_bookId, COALESCE(SUM(ab.quantity), 0) AS arrival
                  FROM BookCopy as ta_bc
                  JOIN ArrivalBook AS ab
                  GROUP BY ta_bc.book.id) ta ON ta.ta_bookId = b.id
          GROUP BY b.id, b.title
          HAVING (ta.arrival - ts.sold) <= 3
          """)
  List<StockProjection> findLowStockBooks();
}
