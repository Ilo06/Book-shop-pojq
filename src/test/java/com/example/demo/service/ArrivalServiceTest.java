package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.ArrivalBookRepository;
import com.example.demo.repository.bookshop.ArrivalRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrivalServiceTest {

  private ArrivalRepository arrivalRepository;
  private ArrivalBookRepository arrivalBookRepository;
  private BookCopyRepository bookCopyRepository;
  private ArrivalService arrivalService;

  private UUID arrivalId;
  private UUID bookCopyId;
  private Arrival arrival;
  private BookCopy bookCopy;

  @BeforeEach
  void setUp() {
    arrivalRepository = mock(ArrivalRepository.class);
    arrivalBookRepository = mock(ArrivalBookRepository.class);
    bookCopyRepository = mock(BookCopyRepository.class);
    arrivalService =
        new ArrivalService(arrivalRepository, arrivalBookRepository, bookCopyRepository);

    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();

    BookCopyPrice price =
        BookCopyPrice.builder().id(UUID.randomUUID()).price(BigDecimal.valueOf(19.99)).build();

    bookCopy =
        BookCopy.builder()
            .id(bookCopyId)
            .type(BookCopyType.PAPERBACK)
            .prices(List.of(price))
            .location("Shelf A1")
            .build();

    ArrivalBook arrivalBook = new ArrivalBook(null, null, bookCopy, 5);

    arrival =
        Arrival.builder()
            .id(arrivalId)
            .arrivalDateTime(Instant.now())
            .books(List.of(arrivalBook))
            .build();

    arrivalBook.setArrival(arrival);
  }

  @Test
  void findAll_shouldReturnListOfArrivalResponse() {
    when(arrivalRepository.findAll()).thenReturn(List.of(arrival));

    List<ArrivalResponse> result = arrivalService.findAll();

    assertEquals(1, result.size());
    assertEquals(arrivalId, result.getFirst().getId());
    assertNotNull(result.getFirst().getArrivalDateTime());
  }

  @Test
  void findById_shouldReturnArrivalResponse() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));

    ArrivalResponse result = arrivalService.findById(arrivalId);

    assertEquals(arrivalId, result.getId());
    assertNotNull(result.getArrivalDateTime());
    assertEquals(1, result.getBooks().size());
    assertEquals(bookCopyId, result.getBooks().getFirst().getBookCopyId());
    assertEquals(5, result.getBooks().getFirst().getQuantity());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(arrivalRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> arrivalService.findById(id));
  }

  @Test
  void create_shouldReturnArrivalResponse() {
    QuantifiedBookCopyDTO qtyDTO = new QuantifiedBookCopyDTO(bookCopyId, 3);
    CreateArrivalDTO input = new CreateArrivalDTO(Instant.now(), List.of(qtyDTO));

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalRepository.save(any(Arrival.class)))
        .thenAnswer(
            invocation -> {
              Arrival a = invocation.getArgument(0);
              a.setId(arrivalId);
              return a;
            });
    when(arrivalBookRepository.saveAll(any(List.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(bookCopyRepository.findAllById(any(List.class))).thenReturn(List.of(bookCopy));

    ArrivalResponse result = arrivalService.create(input);

    assertNotNull(result.getId());
    assertEquals(arrivalId, result.getId());
    assertNotNull(result.getArrivalDateTime());
    assertEquals(1, result.getBooks().size());
  }

  @Test
  void create_shouldThrowResourceNotFoundExceptionWhenBookCopyNotFound() {
    UUID missingId = UUID.randomUUID();
    QuantifiedBookCopyDTO qtyDTO = new QuantifiedBookCopyDTO(missingId, 1);
    CreateArrivalDTO input = new CreateArrivalDTO(Instant.now(), List.of(qtyDTO));

    when(bookCopyRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> arrivalService.create(input));
    verify(arrivalRepository, never()).save(any());
  }
}
