package com.example.demo.service;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookCopyService {

  private final BookCopyRepository bookCopyRepository;
  private final BookService bookService;

  public List<BookCopyResponse> findAll(BookStatus status) {
    List<BookCopy> copies =
        status != null ? bookCopyRepository.findByStatus(status) : bookCopyRepository.findAll();
    return copies.stream().map(this::toResponse).toList();
  }

  public BookCopyResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  public List<BookCopyResponse> findByBookId(UUID bookId) {
    bookService.getOrThrow(bookId);
    return bookCopyRepository.findByBookId(bookId).stream().map(this::toResponse).toList();
  }

  @Transactional
  public BookCopyResponse create(CreateBookCopyDTO input) {
    Book book = bookService.getOrThrow(input.getBookId());
    BookCopy copy =
        BookCopy.builder()
            .book(book)
            .status(input.getStatus() != null ? input.getStatus() : BookStatus.AVAILABLE)
            .type(input.getType())
            .price(input.getPrice())
            .location(input.getLocation())
            .build();
    return toResponse(bookCopyRepository.save(copy));
  }

  @Transactional
  public BookCopyResponse patch(UUID id, PatchBookCopyDTO input) {
    BookCopy copy = getOrThrow(id);
    if (input.getStatus() != null) {
      copy.setStatus(input.getStatus());
    }
    if (input.getPrice() != null) {
      copy.setPrice(input.getPrice());
    }
    if (input.getLocation() != null) {
      copy.setLocation(input.getLocation());
    }
    return toResponse(bookCopyRepository.save(copy));
  }

  @Transactional
  public void delete(UUID id) {
    BookCopy copy = getOrThrow(id);
    bookCopyRepository.delete(copy);
  }

  public Integer getStock(UUID copyId) {
    return bookCopyRepository.findAvailableCopiesTypePerBookId(copyId);
  }

  public BookCopy getOrThrow(UUID id) {
    return bookCopyRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("BookCopy not found with id: " + id));
  }

  private BookCopyResponse toResponse(BookCopy copy) {
    return BookCopyResponse.builder()
        .id(copy.getId())
        .bookId(copy.getBook() != null ? copy.getBook().getId() : null)
        .type(copy.getType())
        .status(copy.getStatus())
        .price(copy.getPrice())
        .location(copy.getLocation())
        .build();
  }
}
