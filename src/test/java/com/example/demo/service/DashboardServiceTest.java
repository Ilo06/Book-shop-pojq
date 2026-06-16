package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private BookCopyRepository bookCopyRepository;

  @InjectMocks private DashboardService dashboardService;

  @Test
  void getTodayRevenue_returnsValue() {
    when(saleRepository.findTodayRevenue()).thenReturn(BigDecimal.valueOf(100));

    var result = dashboardService.getTodayRevenue();

    assertEquals(BigDecimal.valueOf(100), result);
  }

  @Test
  void getMonthlyRevenue_returnsValue() {
    when(saleRepository.findMonthlyRevenue()).thenReturn(BigDecimal.valueOf(5000));

    var result = dashboardService.getMonthlyRevenue();

    assertEquals(BigDecimal.valueOf(5000), result);
  }

  @Test
  void getTopSellers_returnsList() {
    when(saleRepository.findTopSellers(any(Pageable.class))).thenReturn(List.of());

    var result = dashboardService.getTopSellers(10);

    assertNotNull(result);
    assertEquals(0, result.size());
    verify(saleRepository).findTopSellers(any(Pageable.class));
  }

  @Test
  void getRevenueByGenre_returnsList() {
    when(saleRepository.findRevenueByGenre()).thenReturn(List.of());

    var result = dashboardService.getRevenueByGenre();

    assertNotNull(result);
  }

  @Test
  void getAvailableCopiesPerBook_returnsList() {
    when(bookCopyRepository.findAvailableCopiesPerBook()).thenReturn(List.of());

    var result = dashboardService.getAvailableCopiesPerBook();

    assertNotNull(result);
  }

  @Test
  void getLowStockBooks_returnsList() {
    when(bookCopyRepository.findLowStockBooks()).thenReturn(List.of());

    var result = dashboardService.getLowStockBooks();

    assertNotNull(result);
  }
}
