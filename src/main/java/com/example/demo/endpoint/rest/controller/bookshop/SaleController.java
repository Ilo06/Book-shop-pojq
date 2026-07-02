package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.service.SaleService;

import java.time.Instant;
import java.time.LocalDate;
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
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(saleService.findByDateBetween(from, to));
  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody CreateSaleDTO sale) {
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
}
