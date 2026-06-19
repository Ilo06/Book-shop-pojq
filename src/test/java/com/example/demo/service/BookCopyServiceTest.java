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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

  @Mock BookCopyRepository bookCopyRepository;
  @Mock BookService bookService;
  BookCopyService service;
  Book book;
  BookCopy copy;
  UUID id, bookId;

  @BeforeEach
  void setUp() {
    service = new BookCopyService(bookCopyRepository, bookService);
    id = UUID.randomUUID();
    bookId = UUID.randomUUID();
    book = Book.builder().id(bookId).title("Test").build();
    copy =
        BookCopy.builder()
            .id(id)
            .book(book)
            .status(BookStatus.AVAILABLE)
            .type(BookCopyType.PAPERBACK)
            .price(BigDecimal.TEN)
            .location("A1")
            .build();
  }

  @Test
  void findAll_withStatus_returnsFiltered() {
    when(bookCopyRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(List.of(copy));

    List<BookCopyResponse> result = service.findAll(BookStatus.AVAILABLE);

    assertEquals(1, result.size());
  }

  @Test
  void findAll_withNullStatus_returnsAll() {
    when(bookCopyRepository.findAll()).thenReturn(List.of(copy));

    List<BookCopyResponse> result = service.findAll(null);

    assertEquals(1, result.size());
  }

  @Test
  void findById_returnsCopy() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));

    BookCopyResponse result = service.findById(id);

    assertEquals(BookStatus.AVAILABLE, result.getStatus());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
  }

  @Test
  void findByBookId_returnsCopies() {
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(copy));

    List<BookCopyResponse> result = service.findByBookId(bookId);

    assertEquals(1, result.size());
  }

  @Test
  void create_savesCopy() {
    CreateBookCopyDTO input = new CreateBookCopyDTO(bookId, BookCopyType.HARDBACK, BigDecimal.valueOf(15), "B2", null);
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any())).thenReturn(copy);

    BookCopyResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_withDefaultStatus_saves() {
    CreateBookCopyDTO input = new CreateBookCopyDTO(bookId, BookCopyType.POCKET, BigDecimal.valueOf(5), null, null);
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any())).thenReturn(copy);

    BookCopyResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void patch_updatesFields() {
    PatchBookCopyDTO input = new PatchBookCopyDTO(BookStatus.SOLD_OUT, BigDecimal.valueOf(20), "C3");
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));
    when(bookCopyRepository.save(any())).thenReturn(copy);

    BookCopyResponse result = service.patch(id, input);

    assertNotNull(result);
  }

  @Test
  void patch_partialUpdate() {
    PatchBookCopyDTO input = new PatchBookCopyDTO(null, null, null);
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));
    when(bookCopyRepository.save(any())).thenReturn(copy);

    BookCopyResponse result = service.patch(id, input);

    assertNotNull(result);
  }

  @Test
  void patch_throwsWhenNotFound() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.patch(id, new PatchBookCopyDTO()));
  }

  @Test
  void delete_removesCopy() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));

    service.delete(id);

    verify(bookCopyRepository).delete(copy);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
  }

  @Test
  void getOrThrow_returnsCopy() {
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));

    BookCopy result = service.getOrThrow(id);

    assertEquals(id, result.getId());
  }
}
