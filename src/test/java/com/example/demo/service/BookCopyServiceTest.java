package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateBookCopyDTO;
import com.example.demo.dto.request.PatchBookCopyDTO;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;
  @Mock private BookService bookService;

  @InjectMocks private BookCopyService bookCopyService;

  @Test
  void findAll_withBookId_filtersByBook() {
    var bookId = UUID.randomUUID();
    var book = Book.builder().id(bookId).title("Test").build();
    var copy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .book(book)
            .status(BookStatus.AVAILABLE)
            .price(BigDecimal.TEN)
            .build();
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.findByBookId(bookId)).thenReturn(List.of(copy));

    var result = bookCopyService.findAll(bookId, null);

    assertEquals(1, result.size());
    verify(bookCopyRepository).findByBookId(bookId);
  }

  @Test
  void findAll_withStatus_filtersByStatus() {
    var copy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .status(BookStatus.AVAILABLE)
            .price(BigDecimal.TEN)
            .build();
    when(bookCopyRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(List.of(copy));

    var result = bookCopyService.findAll(null, BookStatus.AVAILABLE);

    assertEquals(1, result.size());
    verify(bookCopyRepository).findByStatus(BookStatus.AVAILABLE);
  }

  @Test
  void findAll_withBookIdAndStatus_filtersByBoth() {
    var bookId = UUID.randomUUID();
    var book = Book.builder().id(bookId).title("Test").build();
    var copy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .book(book)
            .status(BookStatus.AVAILABLE)
            .price(BigDecimal.TEN)
            .build();
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.findByBookIdAndStatus(bookId, BookStatus.AVAILABLE))
        .thenReturn(List.of(copy));

    var result = bookCopyService.findAll(bookId, BookStatus.AVAILABLE);

    assertEquals(1, result.size());
    verify(bookCopyRepository).findByBookIdAndStatus(bookId, BookStatus.AVAILABLE);
  }

  @Test
  void findAll_withNoFilters_returnsAll() {
    var copy =
        BookCopy.builder()
            .id(UUID.randomUUID())
            .status(BookStatus.AVAILABLE)
            .price(BigDecimal.TEN)
            .build();
    when(bookCopyRepository.findAll()).thenReturn(List.of(copy));

    var result = bookCopyService.findAll(null, null);

    assertEquals(1, result.size());
    verify(bookCopyRepository).findAll();
  }

  @Test
  void findById_returnsCopy() {
    var id = UUID.randomUUID();
    var copy = BookCopy.builder().id(id).status(BookStatus.AVAILABLE).price(BigDecimal.TEN).build();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));

    var result = bookCopyService.findById(id);

    assertNotNull(result);
  }

  @Test
  void create_savesCopy() {
    var bookId = UUID.randomUUID();
    var book = Book.builder().id(bookId).build();
    var input = new CreateBookCopyDTO(bookId, BigDecimal.valueOf(15), "A1", BookStatus.AVAILABLE);
    when(bookService.getOrThrow(bookId)).thenReturn(book);
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = bookCopyService.create(input);

    assertEquals(BigDecimal.valueOf(15), result.getPrice());
  }

  @Test
  void patch_updatesCopy() {
    var id = UUID.randomUUID();
    var copy = BookCopy.builder().id(id).status(BookStatus.AVAILABLE).price(BigDecimal.TEN).build();
    var input = new PatchBookCopyDTO(BookStatus.SOLD, BigDecimal.valueOf(20), "B2");
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));
    when(bookCopyRepository.save(any(BookCopy.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = bookCopyService.patch(id, input);

    assertEquals(BookStatus.SOLD, result.getStatus());
    assertEquals(BigDecimal.valueOf(20), result.getPrice());
    assertEquals("B2", result.getLocation());
  }

  @Test
  void delete_removesCopy() {
    var id = UUID.randomUUID();
    var copy = BookCopy.builder().id(id).build();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.of(copy));

    bookCopyService.delete(id);

    verify(bookCopyRepository).delete(copy);
  }

  @Test
  void getOrThrow_throwsWhenNotFound() {
    var id = UUID.randomUUID();
    when(bookCopyRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookCopyService.findById(id));
  }
}
