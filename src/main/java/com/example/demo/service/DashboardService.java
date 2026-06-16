package com.example.demo.service;

import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import com.example.demo.repository.projection.RevenueByGenreProjection;
import com.example.demo.repository.projection.StockProjection;
import com.example.demo.repository.projection.TopSellerProjection;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final SaleRepository saleRepository;
  private final BookCopyRepository bookCopyRepository;

  public BigDecimal getTodayRevenue() {
    return saleRepository.findTodayRevenue();
  }

  public BigDecimal getMonthlyRevenue() {
    return saleRepository.findMonthlyRevenue();
  }

  public List<TopSellerProjection> getTopSellers(int limit) {
    return saleRepository.findTopSellers(PageRequest.of(0, limit));
  }

  public List<RevenueByGenreProjection> getRevenueByGenre() {
    return saleRepository.findRevenueByGenre();
  }

  public List<StockProjection> getAvailableCopiesPerBook() {
    return bookCopyRepository.findAvailableCopiesPerBook();
  }

  public List<StockProjection> getLowStockBooks() {
    return bookCopyRepository.findLowStockBooks();
  }
}
