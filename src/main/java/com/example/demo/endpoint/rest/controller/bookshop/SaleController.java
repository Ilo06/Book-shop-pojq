package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.entity.Sale;
import com.example.demo.service.SaleService;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {
  private final SaleService saleService;

  @GetMapping
  public ResponseEntity<?> getAll(
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to) {
    return ResponseEntity.ok(saleService.findByDateBetween(from, to));
  }

  @PostMapping
  public ResponseEntity<?> save(@RequestBody Sale sale) {
    return ResponseEntity.ok(saleService.save(sale));
  }

  @GetMapping("/{saleId}")
  public ResponseEntity<?> getById(@PathVariable UUID saleId) {
    return ResponseEntity.ok(saleService.findById(saleId));
  }
}
