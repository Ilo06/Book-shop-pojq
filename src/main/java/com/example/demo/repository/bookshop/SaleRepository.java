package com.example.demo.repository.bookshop;

import com.example.demo.entity.Sale;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

  List<Sale> findBySaleDateBetween(LocalDate from, LocalDate to);

  @Query(
      """
      SELECT COALESCE(SUM(sbc.unitPrice * sbc.quantity), 0)
      FROM Sale s
      JOIN s.books sbc
      WHERE s.saleDate = CURRENT_DATE
      """)
  Double findTodayRevenue();

  @Query(
      """
      SELECT COALESCE(SUM(sbc.unitPrice * sbc.quantity), 0)
      FROM Sale s
      JOIN s.books sbc
      WHERE YEAR(s.saleDate)  = YEAR(CURRENT_DATE)
        AND MONTH(s.saleDate) = MONTH(CURRENT_DATE)
      """)
  Double findMonthlyRevenue();

  @Query(
      """
      SELECT b.id, b.title, SUM(sbc.quantity)
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      GROUP BY b.id, b.title
      ORDER BY SUM(sbc.quantity) DESC
      LIMIT :limit
      """)
  List<Object[]> findTopSellers(@Param("limit") int limit);

  @Query(
      """
      SELECT g.id, g.name, SUM(sbc.unitPrice * sbc.quantity)
      FROM Sale s
      JOIN s.books sbc
      JOIN sbc.bookCopy bc
      JOIN bc.book b
      JOIN b.genre g
      GROUP BY g.id, g.name
      """)
  List<Object[]> findRevenueByGenre();
}
