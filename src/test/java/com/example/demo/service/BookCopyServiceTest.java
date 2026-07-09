package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.BookCopyPrice;
import com.example.demo.entity.Genre;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyPriceRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCopyServiceTest {

  private BookCopyRepository bookCopyRepository;
  private BookService bookService;
  private BookCopyPriceRepository bookCopyPriceRepository;
  private BookCopyService bookCopyService;

  private UUID bookId;
  private UUID copyId;
  private Book book;
  private BookCopy bookCopy;
  private CreateBookCopyDTO createBookCopyDTO;

  @BeforeEach
  void setUp() {
    bookCopyRepository = mock(BookCopyRepository.class);
    bookService = mock(BookService.class);
    bookCopyPriceRepository = mock(BookCopyPriceRepository.class);
    bookCopyService = new BookCopyService(bookCopyRepository, bookService, bookCopyPriceRepository);

    bookId = UUID.randomUUID();
    copyId = UUID.randomUUID();

    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();
    book =
        Book.builder()
            .id(bookId)
            .title("Test Book")
            .isbn("9781234567890")
            .publishDate(LocalDate.of(2026, 1, 1))
            .genres(List.of(genre))
            .build();

    BookCopyPrice price =
        BookCopyPrice.builder().id(UUID.randomUUID()).price(BigDecimal.valueOf(19.99)).build();

    bookCopy =
        BookCopy.builder()
            .id(copyId)
            .book(book)
            .type(BookCopyType.PAPERBACK)
            .prices(List.of(price))
            .location("Shelf A1")
            .build();

    createBookCopyDTO = new CreateBookCopyDTO();
    createBookCopyDTO.setType(BookCopyType.HARDBACK);
    createBookCopyDTO.setPrice(BigDecimal.valueOf(29.99));
    createBookCopyDTO.setLocation("Shelf B2");
  }

  @Test
  void findById_shouldReturnBookCopyResponse() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    BookCopyResponse result = bookCopyService.findById(copyId);

    assertEquals(copyId, result.getId());
    assertEquals(bookId, result.getBookId());
    assertEquals(BookCopyType.PAPERBACK, result.getType());
    assertEquals("Shelf A1", result.getLocation());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.findById(id));
  }

  @Test
  void findByBookId_shouldReturnListOfBookCopyResponse() {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(bookCopy));

    List<BookCopyResponse> result = bookCopyService.findByBookId(bookId);

    assertEquals(1, result.size());
    assertEquals(copyId, result.getFirst().getId());
  }

  @Test
  void findByBookId_shouldThrowResourceNotFoundExceptionWhenBookNotFound() {
    UUID id = UUID.randomUUID();
    when(bookService.getOrThrow(id))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + id));

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.findByBookId(id));
  }

  @Test
  void create_shouldReturnBookCopyResponse() {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0, BookCopy.class));
    System.out.println(bookCopyService.create(bookId, createBookCopyDTO));

    BookCopyResponse result = bookCopyService.create(bookId, createBookCopyDTO);

    assertEquals(bookId, result.getBookId());
    assertEquals(BookCopyType.HARDBACK, result.getType());
    assertEquals("Shelf B2", result.getLocation());
  }

  @Test
  void create_shouldThrowResourceNotFoundExceptionWhenBookNotFound() {
    UUID id = UUID.randomUUID();
    when(bookService.getOrThrow(id))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + id));

    assertThrows(
        ResourceNotFoundException.class, () -> bookCopyService.create(id, createBookCopyDTO));
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  void patch_shouldUpdatePriceAndLocation() {
    PatchBookCopyDTO patchDTO = new PatchBookCopyDTO();
    patchDTO.setPrice(BigDecimal.valueOf(24.99));
    patchDTO.setLocation("Shelf C3");

    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookCopyResponse result = bookCopyService.patch(copyId, patchDTO);

    assertEquals(copyId, result.getId());
  }

  @Test
  void patch_shouldUpdatePriceOnly() {
    PatchBookCopyDTO patchDTO = new PatchBookCopyDTO();
    patchDTO.setPrice(BigDecimal.valueOf(14.99));

    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookCopyResponse result = bookCopyService.patch(copyId, patchDTO);

    assertEquals(copyId, result.getId());
  }

  @Test
  void patch_shouldUpdateLocationOnly() {
    PatchBookCopyDTO patchDTO = new PatchBookCopyDTO();
    patchDTO.setLocation("Shelf D4");

    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookCopyResponse result = bookCopyService.patch(copyId, patchDTO);

    assertEquals(copyId, result.getId());
  }

  @Test
  void patch_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    PatchBookCopyDTO patchDTO = new PatchBookCopyDTO();
    patchDTO.setLocation("Shelf Z9");

    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.patch(id, patchDTO));
    verify(bookCopyRepository, never()).save(any());
  }

  @Test
  void delete_shouldDeleteBookCopy() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    bookCopyService.delete(copyId);

    verify(bookCopyPriceRepository).deleteBookCopyPriceByBookCopy(bookCopy);
    verify(bookCopyRepository).delete(bookCopy);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.delete(id));
    verify(bookCopyPriceRepository, never()).deleteBookCopyPriceByBookCopy(any());
    verify(bookCopyRepository, never()).delete(any());
  }

  @Test
  void getStockByCopyId_shouldReturnStock() {
    when(bookCopyRepository.getBookCopyStockByCopyId(copyId)).thenReturn(5);

    Integer result = bookCopyService.getStockByCopyId(copyId);

    assertEquals(5, result);
  }

  @Test
  void getOrThrow_shouldReturnBookCopy() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    BookCopy result = bookCopyService.getOrThrow(copyId);

    assertEquals(copyId, result.getId());
  }

  @Test
  void getOrThrow_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.getOrThrow(id));
  }
}
