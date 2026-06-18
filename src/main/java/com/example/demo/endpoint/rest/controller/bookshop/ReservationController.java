package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.request.PatchReservationDTO;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping
  public ResponseEntity<List<ReservationResponse>> listReservations(
      @RequestParam(required = false) ReservationStatus status) {
    return ResponseEntity.ok(reservationService.findAll(status));
  }

  @GetMapping("/{reservationId}")
  public ResponseEntity<ReservationResponse> getReservation(@PathVariable UUID reservationId) {
    return ResponseEntity.ok(reservationService.findById(reservationId));
  }

  @PostMapping
  public ResponseEntity<ReservationResponse> createReservation(
      @Valid @RequestBody CreateReservationDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(input));
  }

  @PatchMapping("/{reservationId}")
  public ResponseEntity<ReservationResponse> patchReservation(
      @PathVariable UUID reservationId, @Valid @RequestBody PatchReservationDTO input) {
    return ResponseEntity.ok(reservationService.patch(reservationId, input));
  }

  @DeleteMapping("/{reservationId}")
  public ResponseEntity<Void> deleteReservation(@PathVariable UUID reservationId) {
    reservationService.delete(reservationId);
    return ResponseEntity.noContent().build();
  }
}
