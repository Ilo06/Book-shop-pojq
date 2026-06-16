package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.service.ArrivalService;
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
@RequestMapping("/api/v1/arrivals")
@RequiredArgsConstructor
public class ArrivalController {

  private final ArrivalService arrivalService;

  @GetMapping
  public ResponseEntity<Page<ArrivalResponse>> listArrivals(
      @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(arrivalService.findAll(pageable));
  }

  @GetMapping("/{arrivalId}")
  public ResponseEntity<ArrivalResponse> getArrival(@PathVariable UUID arrivalId) {
    return ResponseEntity.ok(arrivalService.findById(arrivalId));
  }

  @PostMapping
  public ResponseEntity<ArrivalResponse> createArrival(@Valid @RequestBody CreateArrivalDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(arrivalService.create(input));
  }

  @DeleteMapping("/{arrivalId}")
  public ResponseEntity<Void> deleteArrival(@PathVariable UUID arrivalId) {
    arrivalService.delete(arrivalId);
    return ResponseEntity.noContent().build();
  }
}
