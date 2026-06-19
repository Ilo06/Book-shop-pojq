package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.math.BigDecimal;
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
class SaleServiceTest {

  @Mock SaleRepository saleRepository;
  @Mock SaleBookCopyRepository saleBookCopyRepository;
  @Mock BookCopyRepository bookCopyRepository;
  SaleService service;
  Sale sale;
  BookCopy bookCopy;
  UUID saleId, bookCopyId;

  @BeforeEach
  void setUp() {
    service = new SaleService(saleRepository, saleBookCopyRepository, bookCopyRepository);
    saleId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();
    bookCopy =
        BookCopy.builder().id(bookCopyId).price(BigDecimal.valueOf(20)).build();
    sale = Sale.builder().id(saleId).saleDate(LocalDate.now()).build();
    sale.setBooks(java.util.List.of());
  }

  @Test
  void findByDateBetween_withBothDates() {
    LocalDate from = LocalDate.of(2024, 1, 1);
    LocalDate to = LocalDate.of(2024, 12, 31);
    when(saleRepository.findBySaleDateBetween(from, to)).thenReturn(List.of(sale));

    List<SaleResponse> result = service.findByDateBetween(from, to);

    assertEquals(1, result.size());
  }

  @Test
  void findByDateBetween_withNullFrom() {
    LocalDate to = LocalDate.now();
    when(saleRepository.findBySaleDateBetween(LocalDate.of(1970, 1, 1), to)).thenReturn(List.of(sale));

    List<SaleResponse> result = service.findByDateBetween(null, to);

    assertEquals(1, result.size());
  }

  @Test
  void findByDateBetween_withNullTo() {
    LocalDate from = LocalDate.of(2024, 1, 1);
    when(saleRepository.findBySaleDateBetween(from, LocalDate.now())).thenReturn(List.of(sale));

    List<SaleResponse> result = service.findByDateBetween(from, null);

    assertEquals(1, result.size());
  }

  @Test
  void findByDateBetween_withBothNull() {
    when(saleRepository.findBySaleDateBetween(LocalDate.of(1970, 1, 1), LocalDate.now()))
        .thenReturn(List.of(sale));

    List<SaleResponse> result = service.findByDateBetween(null, null);

    assertEquals(1, result.size());
  }

  @Test
  void save_createsSale() {
    CreateSaleDTO input = new CreateSaleDTO(LocalDate.now(), List.of(bookCopyId));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(saleRepository.save(any())).thenReturn(sale);
    when(saleBookCopyRepository.saveAll(any())).thenReturn(List.of());

    SaleResponse result = service.save(input);

    assertNotNull(result);
  }

  @Test
  void save_throwsWhenBookCopyNotFound() {
    CreateSaleDTO input = new CreateSaleDTO(LocalDate.now(), List.of(bookCopyId));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.save(input));
  }

  @Test
  void findById_returnsSale() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.findBySaleId(saleId)).thenReturn(List.of());

    SaleResponse result = service.findById(saleId);

    assertNotNull(result);
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(saleId));
  }

  @Test
  void toResponse_containsBookItems() {
    sale.setBooks(List.of());
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.findBySaleId(saleId)).thenReturn(List.of());

    SaleResponse result = service.findById(saleId);

    assertNotNull(result.getBooks());
  }
}
