package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.service.BookCopyService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-copies")
@RequiredArgsConstructor
public class BookCopyController {

  private final BookCopyService bookCopyService;

  @GetMapping
  public ResponseEntity<List<BookCopyResponse>> listBookCopies(
      @RequestParam(required = false) UUID bookId,
      @RequestParam(required = false) BookStatus status) {
    return ResponseEntity.ok(bookCopyService.findAll(bookId, status));
  }

  @GetMapping("/{copyId}")
  public ResponseEntity<BookCopyResponse> getBookCopy(@PathVariable UUID copyId) {
    return ResponseEntity.ok(bookCopyService.findById(copyId));
  }

  @PostMapping
  public ResponseEntity<BookCopyResponse> createBookCopy(
      @Valid @RequestBody CreateBookCopyDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookCopyService.create(input));
  }

  @PatchMapping("/{copyId}")
  public ResponseEntity<BookCopyResponse> patchBookCopy(
      @PathVariable UUID copyId, @Valid @RequestBody PatchBookCopyDTO input) {
    return ResponseEntity.ok(bookCopyService.patch(copyId, input));
  }

  @DeleteMapping("/{copyId}")
  public ResponseEntity<Void> deleteBookCopy(@PathVariable UUID copyId) {
    bookCopyService.delete(copyId);
    return ResponseEntity.noContent().build();
  }
}
