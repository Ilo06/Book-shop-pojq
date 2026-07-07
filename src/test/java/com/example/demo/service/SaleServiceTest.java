package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.RevenueResponse;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.BookCopyPrice;
import com.example.demo.entity.Genre;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.entity.enums.BookCopyType;
import com.example.demo.entity.enums.SaleStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import com.example.demo.repository.projection.TopSellerProjection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

class SaleServiceTest {

  private SaleRepository saleRepository;
  private SaleBookCopyRepository saleBookCopyRepository;
  private BookCopyRepository bookCopyRepository;
  private SaleService saleService;

  private UUID saleId;
  private UUID bookCopyId;
  private UUID saleBookCopyId;
  private Sale sale;
  private BookCopy bookCopy;

  @BeforeEach
  void setUp() {
    saleRepository = mock(SaleRepository.class);
    saleBookCopyRepository = mock(SaleBookCopyRepository.class);
    bookCopyRepository = mock(BookCopyRepository.class);
    saleService = new SaleService(saleRepository, saleBookCopyRepository, bookCopyRepository);

    saleId = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();
    saleBookCopyId = UUID.randomUUID();

    Genre genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();

    Book book =
        Book.builder()
            .id(UUID.randomUUID())
            .title("Test Book")
            .isbn("9781234567890")
            .publishDate(java.time.LocalDate.of(2024, 1, 1))
            .genres(List.of(genre))
            .build();

    BookCopyPrice price =
        BookCopyPrice.builder().id(UUID.randomUUID()).price(BigDecimal.valueOf(19.99)).build();

    bookCopy =
        BookCopy.builder()
            .id(bookCopyId)
            .book(book)
            .type(BookCopyType.PAPERBACK)
            .prices(List.of(price))
            .location("Shelf A1")
            .build();

    sale =
        Sale.builder()
            .id(saleId)
            .status(SaleStatus.PENDING)
            .isReservation(false)
            .creationDateTime(Instant.now())
            .build();

    SaleBookCopy saleBookCopy =
        SaleBookCopy.builder().id(saleBookCopyId).sale(sale).bookCopy(bookCopy).quantity(2).build();

    sale.setBooks(List.of(saleBookCopy));
  }

  @Test
  void findByDateBetween_shouldReturnSalesWithoutDateFilter() {
    when(saleRepository.findByCreationDateTimeBetween(any(Instant.class), any(Instant.class)))
        .thenReturn(List.of(sale));

    List<SaleResponse> result = saleService.findByDateBetween(null, null);

    assertEquals(1, result.size());
    assertEquals(saleId, result.getFirst().getId());
  }

  @Test
  void findByDateBetween_shouldReturnSalesWithDateFilter() {
    Instant from = Instant.parse("2024-01-01T00:00:00Z");
    Instant to = Instant.parse("2024-12-31T23:59:59Z");
    when(saleRepository.findByCreationDateTimeBetween(from, to)).thenReturn(List.of(sale));

    List<SaleResponse> result = saleService.findByDateBetween(from, to);

    assertEquals(1, result.size());
    assertEquals(saleId, result.getFirst().getId());
  }

  @Test
  void save_shouldReturnSaleResponse() throws BadRequestException {
    CreateSaleDTO createSaleDTO = new CreateSaleDTO();
    createSaleDTO.setCreationDateTime(Instant.now());
    createSaleDTO.setQuantifiedBookCopyList(List.of(new QuantifiedBookCopyDTO(bookCopyId, 2)));
    createSaleDTO.setIsReservation(false);

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(bookCopyRepository.getBookCopyStockByCopyId(bookCopyId)).thenReturn(2);
    when(saleRepository.save(any(Sale.class)))
        .thenAnswer(
            invocation -> {
              Sale s = invocation.getArgument(0);
              s.setId(saleId);
              return s;
            });
    when(saleBookCopyRepository.saveAll(any(List.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SaleResponse result = saleService.save(createSaleDTO);

    assertNotNull(result.getId());
    assertEquals(saleId, result.getId());
    assertNotNull(result.getBooks());
    assertEquals(1, result.getBooks().size());
  }

  @Test
  void save_shouldThrowResourceNotFoundExceptionWhenBookCopyNotFound() {
    UUID missingId = UUID.randomUUID();
    CreateSaleDTO createSaleDTO = new CreateSaleDTO();
    createSaleDTO.setCreationDateTime(Instant.now());
    createSaleDTO.setQuantifiedBookCopyList(List.of(new QuantifiedBookCopyDTO(missingId, 1)));
    createSaleDTO.setIsReservation(false);

    when(bookCopyRepository.findById(missingId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> saleService.save(createSaleDTO));
    verify(saleRepository, never()).save(any());
  }

  @Test
  void findById_shouldReturnSaleResponse() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleBookCopyRepository.findBySaleId(saleId)).thenReturn(sale.getBooks());

    SaleResponse result = saleService.findById(saleId);

    assertEquals(saleId, result.getId());
    assertEquals(sale.getStatus(), result.getSaleStatus());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(saleRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> saleService.findById(id));
  }

  @Test
  void finalize_shouldConfirmSale() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

    SaleResponse result = saleService.finalize(saleId, true);

    assertEquals(SaleStatus.CONFIRMED, result.getSaleStatus());
  }

  @Test
  void finalize_shouldRejectSale() {
    when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

    SaleResponse result = saleService.finalize(saleId, false);

    assertEquals(SaleStatus.REJECTED, result.getSaleStatus());
  }

  @Test
  void finalize_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(saleRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> saleService.finalize(id, true));
  }

  @Test
  void getTodayRevenue_shouldReturnRevenue() {
    when(saleRepository.findTodaySale()).thenReturn(List.of(sale));

    RevenueResponse.TodayRevenue result = saleService.getTodayRevenue();

    assertNotNull(result);
    assertNotNull(result.getTodayRevenue());
  }

  @Test
  void getTodayRevenue_shouldReturnZeroWhenNoSales() {
    when(saleRepository.findTodaySale()).thenReturn(List.of());

    RevenueResponse.TodayRevenue result = saleService.getTodayRevenue();

    assertEquals(BigDecimal.ZERO, result.getTodayRevenue());
  }

  @Test
  void getMonthlyRevenue_shouldReturnRevenue() {
    when(saleRepository.findMonthlySale()).thenReturn(List.of(sale));

    RevenueResponse.MonthlyRevenue result = saleService.getMonthlyRevenue();

    assertNotNull(result);
    assertNotNull(result.getMonthlyRevenue());
  }

  @Test
  void getTopSellers_shouldReturnEntries() {
    TopSellerProjection projection = mock(TopSellerProjection.class);
    when(projection.getBookId()).thenReturn(UUID.randomUUID());
    when(projection.getTitle()).thenReturn("Best Seller");
    when(projection.getTotalSold()).thenReturn(50L);

    when(saleRepository.findTopSellers(PageRequest.of(0, 5))).thenReturn(List.of(projection));

    List<RevenueResponse.TopSellerEntry> result = saleService.getTopSellers(5);

    assertEquals(1, result.size());
    assertEquals("Best Seller", result.getFirst().getTitle());
    assertEquals(50L, result.getFirst().getUnitsSold());
  }

  @Test
  void getTopSellers_shouldUseDefaultLimit() {
    when(saleRepository.findTopSellers(PageRequest.of(0, 10))).thenReturn(List.of());

    List<RevenueResponse.TopSellerEntry> result = saleService.getTopSellers(10);

    assertTrue(result.isEmpty());
  }

  @Test
  void getRevenueByGenre_shouldReturnEntries() {
    when(saleRepository.findAllConfirmedSale()).thenReturn(List.of(sale));

    List<RevenueResponse.RevenueByGenreEntry> result = saleService.getRevenueByGenre();

    assertEquals(1, result.size());
    assertNotNull(result.getFirst().getGenreName());
    assertTrue(result.getFirst().getRevenue().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void getRevenueByGenre_shouldReturnEmptyWhenNoSales() {
    when(saleRepository.findAllConfirmedSale()).thenReturn(List.of());

    List<RevenueResponse.RevenueByGenreEntry> result = saleService.getRevenueByGenre();

    assertTrue(result.isEmpty());
  }
}
