package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.request.CreateBookDTO;
import com.example.demo.dto.response.BookResponse;
import com.example.demo.dto.response.BookSummaryResponse;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping
  public ResponseEntity<List<BookSummaryResponse>> listBooks(
      @RequestParam(required = false) UUID genreId,
      @RequestParam(required = false) UUID authorId,
      @RequestParam(required = false) String search) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookService.findAll(genreId, authorId, search));
  }

  @GetMapping("/{bookId}")
  public ResponseEntity<BookResponse> getBook(@PathVariable UUID bookId) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookService.findById(bookId));
  }

  @PostMapping
  public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookDTO input) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Content-Type", "application/json")
        .body(bookService.create(input));
  }

  @PutMapping("/{bookId}")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable UUID bookId, @Valid @RequestBody CreateBookDTO input) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookService.update(bookId, input));
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID bookId) {
    bookService.delete(bookId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/{bookId}/stock")
  public ResponseEntity<?> findAvailableCopiesPerBook(
      @PathVariable UUID bookId,
      @RequestParam(required = false, defaultValue = "false") Boolean detailed) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(detailed ? bookService.getDetailedStock(bookId) : bookService.getStock(bookId));
  }

  @GetMapping("/stock")
  public ResponseEntity<?> getAllBooksStock() {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookService.getBooksInStock());
  }

  @GetMapping("/stock/low-stock")
  public ResponseEntity<?> findLowStockBooks() {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(bookService.getLowStock());
  }
}
