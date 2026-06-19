package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import com.example.demo.repository.projection.RevenueByGenreProjection;
import com.example.demo.repository.projection.StockProjection;
import com.example.demo.repository.projection.TopSellerProjection;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock SaleRepository saleRepository;
  @Mock BookCopyRepository bookCopyRepository;
  DashboardService service;

  @BeforeEach
  void setUp() {
    service = new DashboardService(saleRepository, bookCopyRepository);
  }

  @Test
  void getTodayRevenue() {
    when(saleRepository.findTodayRevenue()).thenReturn(BigDecimal.valueOf(100));

    DashboardResponse.TodayRevenue result = service.getTodayRevenue();

    assertEquals(BigDecimal.valueOf(100), result.getTodayRevenue());
  }

  @Test
  void getMonthlyRevenue() {
    when(saleRepository.findMonthlyRevenue()).thenReturn(BigDecimal.valueOf(5000));

    DashboardResponse.MonthlyRevenue result = service.getMonthlyRevenue();

    assertEquals(BigDecimal.valueOf(5000), result.getMonthlyRevenue());
  }

  @Test
  void getBooksInStock() {
    StockProjection proj = mock(StockProjection.class);
    when(proj.getBookId()).thenReturn(UUID.randomUUID());
    when(proj.getTitle()).thenReturn("Book A");
    when(proj.getAvailableCopies()).thenReturn(10L);
    when(bookCopyRepository.findAvailableCopiesPerBook()).thenReturn(List.of(proj));

    List<DashboardResponse.StockEntry> result = service.getBooksInStock();

    assertEquals(1, result.size());
    assertEquals("Book A", result.get(0).getTitle());
    assertEquals(10L, result.get(0).getAvailableCopies());
  }

  @Test
  void getLowStock() {
    StockProjection proj = mock(StockProjection.class);
    when(proj.getBookId()).thenReturn(UUID.randomUUID());
    when(proj.getTitle()).thenReturn("Low Book");
    when(proj.getAvailableCopies()).thenReturn(2L);
    when(bookCopyRepository.findLowStockBooks()).thenReturn(List.of(proj));

    List<DashboardResponse.StockEntry> result = service.getLowStock();

    assertEquals(1, result.size());
    assertEquals("Low Book", result.get(0).getTitle());
  }

  @Test
  void getTopSellers() {
    TopSellerProjection proj = mock(TopSellerProjection.class);
    when(proj.getBookId()).thenReturn(UUID.randomUUID());
    when(proj.getTitle()).thenReturn("Bestseller");
    when(proj.getTotalSold()).thenReturn(50L);
    when(saleRepository.findTopSellers(PageRequest.of(0, 10))).thenReturn(List.of(proj));

    List<DashboardResponse.TopSellerEntry> result = service.getTopSellers(10);

    assertEquals(1, result.size());
    assertEquals("Bestseller", result.get(0).getTitle());
    assertEquals(50L, result.get(0).getUnitsSold());
  }

  @Test
  void getRevenueByGenre() {
    RevenueByGenreProjection proj = mock(RevenueByGenreProjection.class);
    when(proj.getGenreId()).thenReturn(UUID.randomUUID());
    when(proj.getGenreName()).thenReturn("Fiction");
    when(proj.getTotalRevenue()).thenReturn(BigDecimal.valueOf(1000));
    when(saleRepository.findRevenueByGenre()).thenReturn(List.of(proj));

    List<DashboardResponse.RevenueByGenreEntry> result = service.getRevenueByGenre();

    assertEquals(1, result.size());
    assertEquals("Fiction", result.get(0).getGenreName());
    assertEquals(BigDecimal.valueOf(1000), result.get(0).getRevenue());
  }
}
