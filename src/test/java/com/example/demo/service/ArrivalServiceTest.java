package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.entity.Arrival;
import com.example.demo.entity.BookCopy;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.ArrivalBookRepository;
import com.example.demo.repository.bookshop.ArrivalRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArrivalServiceTest {

  @Mock ArrivalRepository arrivalRepository;
  @Mock ArrivalBookRepository arrivalBookRepository;
  @Mock BookCopyRepository bookCopyRepository;
  ArrivalService service;
  Arrival arrival;
  BookCopy bookCopy;
  UUID arrivalId, bookCopyId;

  @BeforeEach
  void setUp() {
    service = new ArrivalService(arrivalRepository, arrivalBookRepository, bookCopyRepository);
    arrivalId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();
    bookCopy = BookCopy.builder().id(bookCopyId).build();
    arrival = Arrival.builder().id(arrivalId).arrivalDate(LocalDate.now()).build();
  }

  @Test
  void findAll_returnsList() {
    when(arrivalRepository.findAll()).thenReturn(List.of(arrival));

    List<ArrivalResponse> result = service.findAll();

    assertEquals(1, result.size());
  }

  @Test
  void findById_returnsArrival() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.of(arrival));

    ArrivalResponse result = service.findById(arrivalId);

    assertNotNull(result);
    assertEquals(arrivalId, result.getId());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(arrivalRepository.findById(arrivalId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(arrivalId));
  }

  @Test
  void create_savesArrival() {
    QuantifiedBookCopyDTO qtyDto = new QuantifiedBookCopyDTO(bookCopyId, 5);
    CreateArrivalDTO input = new CreateArrivalDTO(LocalDate.of(2024, 6, 1), List.of(qtyDto));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalRepository.save(any())).thenReturn(arrival);
    when(arrivalBookRepository.saveAll(any())).thenReturn(List.of());

    ArrivalResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_withDefaultDate() {
    QuantifiedBookCopyDTO qtyDto = new QuantifiedBookCopyDTO(bookCopyId, 3);
    CreateArrivalDTO input = new CreateArrivalDTO(LocalDate.now(), List.of(qtyDto));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(arrivalRepository.save(any())).thenReturn(arrival);
    when(arrivalBookRepository.saveAll(any())).thenReturn(List.of());
    arrival.setBooks(java.util.List.of());

    ArrivalResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_throwsWhenBookCopyNotFound() {
    QuantifiedBookCopyDTO qtyDto = new QuantifiedBookCopyDTO(bookCopyId, 2);
    CreateArrivalDTO input = new CreateArrivalDTO(LocalDate.now(), List.of(qtyDto));
    when(arrivalRepository.save(any())).thenReturn(arrival);
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(input));
  }

  @Test
  void toResponse_withNullBooks_returnsEmptyLines() {
    Arrival nullBooksArrival = Arrival.builder().id(arrivalId).arrivalDate(LocalDate.now()).build();

    when(arrivalRepository.findAll()).thenReturn(List.of(nullBooksArrival));

    List<ArrivalResponse> result = service.findAll();

    assertTrue(result.get(0).getBooks().isEmpty());
  }
}
