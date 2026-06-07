package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateSaleBookCopyDTO;
import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.entity.keys.SaleBookCopyId;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock private SaleRepository saleRepository;

  @Mock private SaleBookCopyRepository saleBookCopyRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @InjectMocks private SaleService saleService;

  private UUID sampleId;
  private Sale sampleSale;

  @BeforeEach
  void setUp() {
    sampleId = UUID.randomUUID();
    sampleSale =
        Sale.builder()
            .id(sampleId)
            .saleDate(LocalDate.now())
            .books(Collections.emptyList())
            .build();
  }

  @Nested
  @DisplayName("Tests for findByDateBetween")
  class FindByDateBetweenTests {

    @Test
    @DisplayName("Should return a list of sales when matching dates exist")
    void shouldReturnSalesBetweenDates() {
      LocalDate from = LocalDate.now().minusDays(7);
      LocalDate to = LocalDate.now();
      List<Sale> expectedSales = List.of(sampleSale);

      when(saleRepository.findBySaleDateBetween(from, to)).thenReturn(expectedSales);

      List<SaleResponse> actualSales = saleService.findByDateBetween(from, to);

      assertEquals(1, actualSales.size());
      assertEquals(sampleId, actualSales.getFirst().getId());
      verify(saleRepository, times(1)).findBySaleDateBetween(from, to);
    }
  }

  @Nested
  @DisplayName("Tests for create")
  class CreateTests {

    @Test
    @DisplayName("Should create sale successfully when book copies are not yet sold")
    void shouldCreateSaleSuccessfully() {
      UUID bookCopyId = UUID.randomUUID();
      BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();

      SaleBookCopyId sbcId = new SaleBookCopyId(sampleId, bookCopyId);

      SaleBookCopy saleBookCopy =
          SaleBookCopy.builder()
              .saleBookCopyId(sbcId)
              .sale(sampleSale)
              .bookCopy(bookCopy)
              .price(BigDecimal.valueOf(12.99))
              .build();

      CreateSaleBookCopyDTO itemDto =
          new CreateSaleBookCopyDTO(bookCopyId, BigDecimal.valueOf(12.99));
      CreateSaleDTO input = new CreateSaleDTO(LocalDate.now(), List.of(itemDto));

      when(saleBookCopyRepository.existsByBookCopyId(bookCopyId)).thenReturn(false);
      when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
      when(saleRepository.save(any(Sale.class))).thenReturn(sampleSale);
      when(saleBookCopyRepository.saveAll(anyList())).thenReturn(List.of(saleBookCopy));

      SaleResponse response = saleService.save(input);

      assertNotNull(response);
      assertEquals(sampleId, response.getId());
      assertEquals(1, response.getBooks().size());
      assertEquals(bookCopyId, response.getBooks().getFirst().getBookCopyId());
      assertEquals(
          0, BigDecimal.valueOf(12.99).compareTo(response.getBooks().getFirst().getPrice()));

      verify(saleBookCopyRepository, times(1)).existsByBookCopyId(bookCopyId);
      verify(bookCopyRepository, times(2)).findById(bookCopyId);
      verify(saleRepository, times(1)).save(any(Sale.class));
      verify(saleBookCopyRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException if any book copy has already been sold")
    void shouldThrowConflictExceptionWhenBookAlreadySold() {
      UUID bookCopyId = UUID.randomUUID();
      CreateSaleBookCopyDTO itemDto =
          new CreateSaleBookCopyDTO(bookCopyId, BigDecimal.valueOf(12.99));
      CreateSaleDTO input = new CreateSaleDTO(LocalDate.now(), List.of(itemDto));

      when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(new BookCopy()));
      when(saleBookCopyRepository.existsByBookCopyId(bookCopyId)).thenReturn(true);

      ResourceConflictException exception =
          assertThrows(ResourceConflictException.class, () -> saleService.save(input));

      assertEquals("Book copy with id: " + bookCopyId + " already sold", exception.getMessage());
      verify(saleBookCopyRepository, times(1)).existsByBookCopyId(bookCopyId);
      verify(saleRepository, never()).save(any(Sale.class));
      verify(saleBookCopyRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when book copy does not exist")
    void shouldThrowNotFoundWhenBookCopyMissing() {
      UUID bookCopyId = UUID.randomUUID();
      CreateSaleBookCopyDTO itemDto =
          new CreateSaleBookCopyDTO(bookCopyId, BigDecimal.valueOf(12.99));
      CreateSaleDTO input = new CreateSaleDTO(LocalDate.now(), List.of(itemDto));

      when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> saleService.save(input));

      assertEquals("Book copy with id: " + bookCopyId + " not found", exception.getMessage());
      verify(saleRepository, never()).save(any(Sale.class));
    }
  }

  @Nested
  @DisplayName("Tests for findById")
  class FindByIdTests {

    @Test
    @DisplayName("Should return sale response when sale exists")
    void shouldReturnSaleWhenIdExists() {
      UUID bookCopyId = UUID.randomUUID();
      BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();
      SaleBookCopyId sbcId = new SaleBookCopyId(sampleId, bookCopyId);
      SaleBookCopy saleBookCopy =
          SaleBookCopy.builder()
              .saleBookCopyId(sbcId)
              .bookCopy(bookCopy)
              .price(BigDecimal.TEN)
              .build();

      when(saleRepository.findById(sampleId)).thenReturn(Optional.of(sampleSale));
      when(saleBookCopyRepository.findBySaleId(sampleId)).thenReturn(List.of(saleBookCopy));

      SaleResponse response = saleService.findById(sampleId);

      assertNotNull(response);
      assertEquals(sampleId, response.getId());
      assertEquals(1, response.getBooks().size());
      assertEquals(bookCopyId, response.getBooks().getFirst().getBookCopyId());
      assertEquals(0, BigDecimal.TEN.compareTo(response.getBooks().getFirst().getPrice()));

      verify(saleRepository, times(1)).findById(sampleId);
      verify(saleBookCopyRepository, times(1)).findBySaleId(sampleId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if sale does not exist")
    void shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
      when(saleRepository.findById(sampleId)).thenReturn(Optional.empty());

      ResourceNotFoundException exception =
          assertThrows(ResourceNotFoundException.class, () -> saleService.findById(sampleId));

      assertEquals("Sale with id " + sampleId + " not found", exception.getMessage());
      verify(saleRepository, times(1)).findById(sampleId);
      verify(saleBookCopyRepository, never()).findBySaleId(any(UUID.class));
    }
  }
}
