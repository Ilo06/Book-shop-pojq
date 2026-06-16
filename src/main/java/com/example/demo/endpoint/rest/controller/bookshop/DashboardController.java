package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.repository.projection.RevenueByGenreProjection;
import com.example.demo.repository.projection.StockProjection;
import com.example.demo.repository.projection.TopSellerProjection;
import com.example.demo.service.DashboardService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/revenue/today")
  public ResponseEntity<BigDecimal> getTodayRevenue() {
    return ResponseEntity.ok(dashboardService.getTodayRevenue());
  }

  @GetMapping("/revenue/monthly")
  public ResponseEntity<BigDecimal> getMonthlyRevenue() {
    return ResponseEntity.ok(dashboardService.getMonthlyRevenue());
  }

  @GetMapping("/top-sellers")
  public ResponseEntity<List<TopSellerProjection>> getTopSellers(
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(dashboardService.getTopSellers(limit));
  }

  @GetMapping("/revenue/by-genre")
  public ResponseEntity<List<RevenueByGenreProjection>> getRevenueByGenre() {
    return ResponseEntity.ok(dashboardService.getRevenueByGenre());
  }

  @GetMapping("/stock/available")
  public ResponseEntity<List<StockProjection>> getAvailableCopies() {
    return ResponseEntity.ok(dashboardService.getAvailableCopiesPerBook());
  }

  @GetMapping("/stock/low")
  public ResponseEntity<List<StockProjection>> getLowStockBooks() {
    return ResponseEntity.ok(dashboardService.getLowStockBooks());
  }
}
