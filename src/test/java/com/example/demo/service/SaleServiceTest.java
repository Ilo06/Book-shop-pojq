package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
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

      List<Sale> actualSales = saleService.findByDateBetween(from, to);

      assertEquals(expectedSales.size(), actualSales.size());
      assertEquals(expectedSales.getFirst().getId(), actualSales.getFirst().getId());
      verify(saleRepository, times(1)).findBySaleDateBetween(from, to);
    }
  }

  @Nested
  @DisplayName("Tests for getAll")
  class GetAllTests {

    @Test
    @DisplayName("Should return all existing sales")
    void shouldReturnAllSales() {
      List<Sale> expectedSales = List.of(sampleSale);
      when(saleRepository.findAll()).thenReturn(expectedSales);

      List<Sale> actualSales = saleService.getAll();

      assertEquals(expectedSales.size(), actualSales.size());
      verify(saleRepository, times(1)).findAll();
    }
  }

  @Nested
  @DisplayName("Tests for save")
  class SaveTests {

    @Test
    @DisplayName("Should save sale successfully when book copies are not yet sold")
    void shouldSaveSaleSuccessfully() {
      UUID bookCopyId = UUID.randomUUID();
      BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();

      SaleBookCopy saleBookCopy = new SaleBookCopy();
      saleBookCopy.setBookCopy(bookCopy);

      Sale saleToSave =
          Sale.builder().saleDate(LocalDate.now()).books(List.of(saleBookCopy)).build();

      when(saleBookCopyRepository.existsByBookCopyId(bookCopyId)).thenReturn(false);
      when(saleRepository.save(any(Sale.class))).thenReturn(sampleSale);
      when(saleBookCopyRepository.saveAll(anyList())).thenReturn(List.of(saleBookCopy));

      Sale savedSale = saleService.save(saleToSave);

      assertNotNull(savedSale);
      assertEquals(sampleId, savedSale.getId());
      verify(saleBookCopyRepository, times(1)).existsByBookCopyId(bookCopyId);
      verify(saleRepository, times(1)).save(saleToSave);
      verify(saleBookCopyRepository, times(1)).saveAll(saleToSave.getBooks());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException if any book copy has already been sold")
    void shouldThrowConflictExceptionWhenBookAlreadySold() {
      UUID bookCopyId = UUID.randomUUID();
      BookCopy bookCopy = BookCopy.builder().id(bookCopyId).build();

      SaleBookCopy saleBookCopy = new SaleBookCopy();
      saleBookCopy.setBookCopy(bookCopy);

      Sale saleToSave =
          Sale.builder().saleDate(LocalDate.now()).books(List.of(saleBookCopy)).build();

      when(saleBookCopyRepository.existsByBookCopyId(bookCopyId)).thenReturn(true);

      ResourceConflictException exception =
          assertThrows(ResourceConflictException.class, () -> saleService.save(saleToSave));

      assertEquals("Book copy has already been sold", exception.getMessage());
      verify(saleBookCopyRepository, times(1)).existsByBookCopyId(bookCopyId);
      verify(saleRepository, never()).save(any(Sale.class));
      verify(saleBookCopyRepository, never()).saveAll(anyList());
    }
  }

  @Nested
  @DisplayName("Tests for findById")
  class FindByIdTests {

    @Test
    @DisplayName("Should return sale and its associated books if sale exists")
    void shouldReturnSaleWhenIdExists() {
      SaleBookCopy mockItem = new SaleBookCopy();
      List<SaleBookCopy> expectedBooks = List.of(mockItem);

      when(saleRepository.findById(sampleId)).thenReturn(Optional.of(sampleSale));
      when(saleBookCopyRepository.findBySaleId(sampleId)).thenReturn(expectedBooks);

      Sale foundSale = saleService.findById(sampleId);

      assertNotNull(foundSale);
      assertEquals(sampleId, foundSale.getId());
      assertEquals(expectedBooks, foundSale.getBooks());

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
