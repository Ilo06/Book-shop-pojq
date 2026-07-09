package com.example.demo.repository.bookshop;

import com.example.demo.entity.Sale;
import com.example.demo.repository.projection.TopSellerProjection;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

  List<Sale> findByCreationDateTimeBetween(Instant from, Instant to);

  @Query(
      """
SELECT s FROM Sale s
WHERE EXTRACT(YEAR  FROM s.finalizationDateTime) = EXTRACT(YEAR  FROM CAST(:date AS date))
  AND EXTRACT(MONTH FROM s.finalizationDateTime) = EXTRACT(MONTH FROM CAST(:date AS date))
  AND EXTRACT(DAY FROM s.finalizationDateTime) = EXTRACT(DAY FROM CAST(:date AS date))
  AND s.status = 'CONFIRMED'
""")
  List<Sale> findSaleByDay(@Param("date") LocalDate date);

  @Query(
      """
SELECT s FROM Sale s
WHERE EXTRACT(YEAR  FROM s.finalizationDateTime) = EXTRACT(YEAR  FROM CAST(:date AS date))
  AND EXTRACT(MONTH FROM s.finalizationDateTime) = EXTRACT(MONTH FROM CAST(:date AS date))
  AND s.status = 'CONFIRMED'
""")
  List<Sale> findSaleByMonth(@Param("date") LocalDate date);

  @Query(
      """
      SELECT b.id              AS bookId,
             b.title           AS title,
             SUM(sbc.quantity) AS totalSold
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      WHERE s.status = 'CONFIRMED'
      GROUP BY b.id, b.title
      ORDER BY SUM(sbc.quantity) DESC
      LIMIT :limit
      """)
  List<TopSellerProjection> findTopSellers(@Param("limit") int limit);

  @Query(
      """
      SELECT s
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      JOIN b.genres g
      WHERE s.status = 'CONFIRMED'
      """)
  List<Sale> findAllConfirmedSale();
}
