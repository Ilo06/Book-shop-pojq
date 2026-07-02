package com.example.demo.service;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.BookCopyPrice;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyPriceRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;

import java.time.Instant;
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
  private final BookCopyPriceRepository bookCopyPriceRepository;

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
    BookCopy bookCopy =
        BookCopy.builder()
            .book(book)
            .type(input.getType())
            .prices(null)
            .location(input.getLocation())
            .build();
    bookCopy.setPrices(List.of(BookCopyPrice.builder().bookCopy(bookCopy).date(Instant.now()).price(input.getPrice()).build()));
    return toResponse(bookCopyRepository.save(bookCopy));
  }

  @Transactional
  public BookCopyResponse patch(UUID id, PatchBookCopyDTO input) {
    BookCopy copy = getOrThrow(id);
    List<BookCopyPrice> prices = copy.getPrices();
    if (input.getPrice() != null) {
      prices.add(BookCopyPrice.builder()
              .bookCopy(copy)
              .date(Instant.now())
              .price(input.getPrice()).build());
      copy.setPrices(prices);
    }
    if (input.getLocation() != null) {
      copy.setLocation(input.getLocation());
    }
    return toResponse(bookCopyRepository.save(copy));
  }

  @Transactional
  public void delete(UUID id) {
    BookCopy copy = getOrThrow(id);
    bookCopyPriceRepository.deleteBookCopyPriceByBookCopy(copy);
    bookCopyRepository.delete(copy);
  }

  public Integer getStockByCopyId(UUID copyId) {
    return bookCopyRepository.getBookCopyStockByCopyId(copyId);
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
        .price(copy.getPrice())
        .location(copy.getLocation())
        .build();
  }
}
