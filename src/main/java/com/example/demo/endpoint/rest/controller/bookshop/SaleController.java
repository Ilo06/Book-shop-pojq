package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.RevenueResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.service.SaleService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {
  private final SaleService saleService;

  @GetMapping
  public ResponseEntity<?> getAll(
      @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.findByDateBetween(from, to));
  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody @Valid CreateSaleDTO sale) throws BadRequestException {
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Content-Type", "application/json")
        .body(saleService.save(sale));
  }

  @GetMapping("/{saleId}")
  public ResponseEntity<?> getById(@PathVariable UUID saleId) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.findById(saleId));
  }

  @PostMapping("/{saleId}/finalize")
  public ResponseEntity<?> finalize(@PathVariable UUID saleId, @RequestParam boolean confirm) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.finalize(saleId, confirm));
  }

  @GetMapping("/day-revenue")
  public ResponseEntity<RevenueResponse.TodayRevenue> getTodayRevenue(
      @RequestParam(required = false) LocalDate t) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.getTodayRevenue((t != null) ? t : LocalDate.now()));
  }

  @GetMapping("/month-revenue")
  public ResponseEntity<RevenueResponse.MonthlyRevenue> getMonthlyRevenue(
      @RequestParam(required = false) LocalDate t) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.getMonthlyRevenue((t != null) ? t : LocalDate.now()));
  }

  @GetMapping("/top-sellers")
  public ResponseEntity<List<RevenueResponse.TopSellerEntry>> getTopSellers(
      @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.getTopSellers(limit));
  }

  @GetMapping("/revenue-by-genre")
  public ResponseEntity<List<RevenueResponse.RevenueByGenreEntry>> getRevenueByGenre() {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.getRevenueByGenre());
  }
}
