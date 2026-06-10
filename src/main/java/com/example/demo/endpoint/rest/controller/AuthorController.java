package com.example.demo.endpoint.rest.controller;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping
  public ResponseEntity<PageResponse<AuthorResponse>> listAuthors(
      @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
    return ResponseEntity.ok(authorService.findAll(pageable));
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
