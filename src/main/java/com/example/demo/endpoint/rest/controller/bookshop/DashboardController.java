package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.service.DashboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/today-revenue")
  public ResponseEntity<DashboardResponse.TodayRevenue> getTodayRevenue() {
    return ResponseEntity.ok(dashboardService.getTodayRevenue());
  }

  @GetMapping("/monthly-revenue")
  public ResponseEntity<DashboardResponse.MonthlyRevenue> getMonthlyRevenue() {
    return ResponseEntity.ok(dashboardService.getMonthlyRevenue());
  }

  @GetMapping("/books-in-stock")
  public ResponseEntity<List<DashboardResponse.StockEntry>> getBooksInStock() {
    return ResponseEntity.ok(dashboardService.getBooksInStock());
  }

  @GetMapping("/low-stock")
  public ResponseEntity<List<DashboardResponse.StockEntry>> getLowStock() {
    return ResponseEntity.ok(dashboardService.getLowStock());
  }

  @GetMapping("/top-sellers")
  public ResponseEntity<List<DashboardResponse.TopSellerEntry>> getTopSellers(
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(dashboardService.getTopSellers(limit));
  }

  @GetMapping("/revenue-by-genre")
  public ResponseEntity<List<DashboardResponse.RevenueByGenreEntry>> getRevenueByGenre() {
    return ResponseEntity.ok(dashboardService.getRevenueByGenre());
  }
}
