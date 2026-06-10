package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.GenreService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @GetMapping
  public ResponseEntity<PageResponse<GenreResponse>> listGenres(
      @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(genreService.findAll(pageable));
  }

  @GetMapping("/{genreId}")
  public ResponseEntity<GenreResponse> getGenre(@PathVariable UUID genreId) {
    return ResponseEntity.ok(genreService.findById(genreId));
  }

  @PostMapping
  public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody CreateGenreDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(input));
  }

  @PutMapping("/{genreId}")
  public ResponseEntity<GenreResponse> updateGenre(
      @PathVariable UUID genreId, @Valid @RequestBody CreateGenreDTO input) {
    return ResponseEntity.ok(genreService.update(genreId, input));
  }

  @DeleteMapping("/{genreId}")
  public ResponseEntity<Void> deleteGenre(@PathVariable UUID genreId) {
    genreService.delete(genreId);
    return ResponseEntity.noContent().build();
  }
}
