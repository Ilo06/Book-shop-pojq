package com.example.demo.repository.bookshop;

import com.example.demo.entity.Sale;
import com.example.demo.repository.projection.RevenueByGenreProjection;
import com.example.demo.repository.projection.TopSellerProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

  List<Sale> findBySaleDateBetween(LocalDate from, LocalDate to);

  @Query(
      """
      SELECT COALESCE(SUM(sbc.price), 0)
      FROM Sale s
      JOIN s.books sbc
      WHERE s.saleDate = CURRENT_DATE
      """)
  BigDecimal findTodayRevenue();

  @Query(
      """
      SELECT COALESCE(SUM(sbc.price), 0)
      FROM Sale s
      JOIN s.books sbc
      WHERE EXTRACT(YEAR  FROM s.saleDate) = EXTRACT(YEAR  FROM CURRENT_DATE)
        AND EXTRACT(MONTH FROM s.saleDate) = EXTRACT(MONTH FROM CURRENT_DATE)
      """)
  BigDecimal findMonthlyRevenue();

  @Query(
      """
      SELECT b.id              AS bookId,
             b.title           AS title,
             COUNT(sbc.bookCopy.id) AS totalSold
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      GROUP BY b.id, b.title
      ORDER BY COUNT(sbc.bookCopy.id) DESC
      """)
  List<TopSellerProjection> findTopSellers(Pageable pageable);

  @Query(
      """
      SELECT g.id                              AS genreId,
             g.name                            AS genreName,
             SUM(sbc.price) AS totalRevenue
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      JOIN b.genre g
      GROUP BY g.id, g.name
      """)
  List<RevenueByGenreProjection> findRevenueByGenre();
}
