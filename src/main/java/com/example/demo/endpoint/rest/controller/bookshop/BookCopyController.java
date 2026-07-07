package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.service.BookCopyService;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books/{bookId}/copies")
@RequiredArgsConstructor
public class BookCopyController {

  private final BookService bookService;
  private final BookCopyService bookCopyService;

  @ModelAttribute
  private void checkIfBookExists(@PathVariable UUID bookId) {
    bookService.getOrThrow(bookId);
  }

  @GetMapping
  public ResponseEntity<List<BookCopyResponse>> listBookCopies(@PathVariable UUID bookId) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookCopyService.findByBookId(bookId));
  }

  @GetMapping("/{copyId}")
  public ResponseEntity<BookCopyResponse> getBookCopy(@PathVariable UUID copyId) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookCopyService.findById(copyId));
  }

  @PostMapping
  public ResponseEntity<BookCopyResponse> createBookCopy(
      @PathVariable UUID bookId,
      @Valid @RequestBody CreateBookCopyDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Content-Type", "application/json")
        .body(bookCopyService.create(bookId, input));
  }

  @PatchMapping("/{copyId}")
  public ResponseEntity<BookCopyResponse> patchBookCopy(
      @PathVariable UUID copyId, @Valid @RequestBody PatchBookCopyDTO input) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookCopyService.patch(copyId, input));
  }

  @DeleteMapping("/{copyId}")
  public ResponseEntity<Void> deleteBookCopy(@PathVariable UUID copyId) {
    bookCopyService.delete(copyId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{copyId}/stock")
  public ResponseEntity<?> getBookCopyStock(@PathVariable UUID copyId) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookCopyService.getStockByCopyId(copyId));
  }
}
