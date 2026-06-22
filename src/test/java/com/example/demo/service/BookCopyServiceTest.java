package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.dto.response.BookCopyResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCopyServiceTest {

  private BookCopyRepository bookCopyRepository;
  private BookService bookService;
  private BookCopyService bookCopyService;

  private UUID bookId;
  private UUID copyId;
  private Book book;
  private BookCopy bookCopy;
  private CreateBookCopyDTO createInput;
  private PatchBookCopyDTO patchInput;

  @BeforeEach
  void setUp() {
    bookCopyRepository = mock(BookCopyRepository.class);
    bookService = mock(BookService.class);
    bookCopyService = new BookCopyService(bookCopyRepository, bookService);

    bookId = UUID.randomUUID();
    copyId = UUID.randomUUID();

    book = Book.builder().id(bookId).title("Test Book").build();

    bookCopy =
        BookCopy.builder()
            .id(copyId)
            .book(book)
            .status(BookStatus.AVAILABLE)
            .type(BookCopyType.PAPERBACK)
            .price(new BigDecimal("19.99"))
            .location("A1")
            .build();

    createInput = new CreateBookCopyDTO();
    createInput.setBookId(bookId);
    createInput.setType(BookCopyType.PAPERBACK);
    createInput.setPrice(new BigDecimal("19.99"));
    createInput.setLocation("A1");

    patchInput = new PatchBookCopyDTO();
    patchInput.setStatus(BookStatus.SOLD_OUT);
  }

  @Test
  void findAll_shouldReturnListOfBookCopyResponse_whenStatusIsNull() {
    when(bookCopyRepository.findAll()).thenReturn(List.of(bookCopy));

    List<BookCopyResponse> result = bookCopyService.findAll(null);

    assertEquals(1, result.size());
    BookCopyResponse response = result.getFirst();
    assertEquals(copyId, response.getId());
    assertEquals(bookId, response.getBookId());
    assertEquals(BookStatus.AVAILABLE, response.getStatus());
    assertEquals(BookCopyType.PAPERBACK, response.getType());
    assertEquals(new BigDecimal("19.99"), response.getPrice());
    assertEquals("A1", response.getLocation());
  }

  @Test
  void findAll_shouldReturnFilteredList_whenStatusIsProvided() {
    when(bookCopyRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(List.of(bookCopy));

    List<BookCopyResponse> result = bookCopyService.findAll(BookStatus.AVAILABLE);

    assertEquals(1, result.size());
    assertEquals(copyId, result.getFirst().getId());
  }

  @Test
  void findById_shouldReturnBookCopyResponse() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    BookCopyResponse result = bookCopyService.findById(copyId);

    assertEquals(copyId, result.getId());
    assertEquals(bookId, result.getBookId());
    assertEquals(BookStatus.AVAILABLE, result.getStatus());
    assertEquals(BookCopyType.PAPERBACK, result.getType());
    assertEquals(new BigDecimal("19.99"), result.getPrice());
    assertEquals("A1", result.getLocation());
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
    assertEquals(bookId, result.getFirst().getBookId());
  }

  @Test
  void findByBookId_shouldThrowResourceNotFoundException_whenBookNotFound() {
    when(bookService.getOrThrow(bookId))
        .thenThrow(new ResourceNotFoundException("Book not found with id: " + bookId));

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.findByBookId(bookId));
  }

  @Test
  void create_shouldReturnBookCopyResponse() {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> {
          BookCopy saved = invocation.getArgument(0);
          saved.setId(copyId);
          return saved;
        });

    BookCopyResponse result = bookCopyService.create(createInput);

    assertNotNull(result.getId());
    assertEquals(bookId, result.getBookId());
    assertEquals(BookCopyType.PAPERBACK, result.getType());
    assertEquals(new BigDecimal("19.99"), result.getPrice());
    assertEquals("A1", result.getLocation());
    assertEquals(BookStatus.AVAILABLE, result.getStatus());
  }

  @Test
  void create_shouldDefaultStatusToAvailable_whenNotProvided() {
    createInput.setStatus(null);
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> {
          BookCopy saved = invocation.getArgument(0);
          saved.setId(copyId);
          return saved;
        });

    BookCopyResponse result = bookCopyService.create(createInput);

    assertEquals(BookStatus.AVAILABLE, result.getStatus());
  }

  @Test
  void patch_shouldUpdateFields() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    BookCopyResponse result = bookCopyService.patch(copyId, patchInput);

    assertEquals(BookStatus.SOLD_OUT, result.getStatus());
  }

  @Test
  void patch_shouldThrowResourceNotFoundException_whenCopyNotFound() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.patch(id, patchInput));
  }

  @Test
  void delete_shouldDeleteCopy() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    bookCopyService.delete(copyId);

    verify(bookCopyRepository).delete(bookCopy);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException_whenCopyNotFound() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.delete(id));
    verify(bookCopyRepository, never()).delete(any());
  }

  @Test
  void getOrThrow_shouldReturnBookCopy() {
    when(bookCopyRepository.findById(copyId)).thenReturn(Optional.of(bookCopy));

    BookCopy result = bookCopyService.getOrThrow(copyId);

    assertEquals(copyId, result.getId());
    assertEquals(book, result.getBook());
  }

  @Test
  void getOrThrow_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.getOrThrow(id));
  }
}
