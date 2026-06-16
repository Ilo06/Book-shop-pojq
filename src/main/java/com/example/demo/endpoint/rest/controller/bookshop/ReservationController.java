package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.service.ReservationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping
  public ResponseEntity<Page<ReservationResponse>> listReservations(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(reservationService.findAll(pageable));
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<ReservationResponse> getReservation(@PathVariable UUID reservationId) {
    return ResponseEntity.ok(reservationService.findById(reservationId));
  }

  @PostMapping
  public ResponseEntity<ReservationResponse> createReservation(
      @Valid @RequestBody CreateReservationDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reservationService.create(input));
  }

  @PatchMapping("/{reservationId}/status")
  public ResponseEntity<ReservationResponse> updateReservationStatus(
      @PathVariable UUID reservationId, @RequestParam ReservationStatus status) {
    return ResponseEntity.ok(reservationService.updateStatus(reservationId, status));
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<Void> deleteReservation(@PathVariable UUID reservationId) {
    reservationService.delete(reservationId);
    return ResponseEntity.noContent().build();
  }
}
