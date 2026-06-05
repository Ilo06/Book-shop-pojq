package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping
  public ResponseEntity<List<AuthorResponse>> listAuthors() {
    return ResponseEntity.ok(authorService.findAll());
  }

  @GetMapping("/{authorId}")
  public ResponseEntity<AuthorResponse> getAuthor(@PathVariable UUID authorId) {
    return ResponseEntity.ok(authorService.findById(authorId));
  }

  @PostMapping
  public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(input));
  }

  @PutMapping("/{authorId}")
  public ResponseEntity<AuthorResponse> updateAuthor(
      @PathVariable UUID authorId, @Valid @RequestBody CreateAuthorDTO input) {
    return ResponseEntity.ok(authorService.update(authorId, input));
  }

  @DeleteMapping("/{authorId}")
  public ResponseEntity<Void> deleteAuthor(@PathVariable UUID authorId) {
    authorService.delete(authorId);
    return ResponseEntity.noContent().build();
  }
}
