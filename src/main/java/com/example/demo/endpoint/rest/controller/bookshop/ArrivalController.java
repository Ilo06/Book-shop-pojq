package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.service.ArrivalService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/arrivals")
@RequiredArgsConstructor
public class ArrivalController {

  private final ArrivalService arrivalService;

  @GetMapping
  public ResponseEntity<List<ArrivalResponse>> listArrivals() {
    return ResponseEntity.ok(arrivalService.findAll());
  }

  @GetMapping("/{arrivalId}")
  public ResponseEntity<ArrivalResponse> getArrival(@PathVariable UUID arrivalId) {
    return ResponseEntity.ok(arrivalService.findById(arrivalId));
  }

  @PostMapping
  public ResponseEntity<ArrivalResponse> createArrival(@Valid @RequestBody CreateArrivalDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(arrivalService.create(input));
  }
}
