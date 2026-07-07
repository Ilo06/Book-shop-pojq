package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.exception.BadRequestException;
import com.example.demo.service.SaleService;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.Valid;
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
  public ResponseEntity<?> finalize(@PathVariable UUID saleId,
                                    @RequestParam boolean confirm) {
    return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(saleService.finalize(saleId, confirm));
  }
}
