package com.example.demo.service;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final SaleRepository saleRepository;
  private final BookCopyRepository bookCopyRepository;

  public DashboardResponse.TodayRevenue getTodayRevenue() {
    return DashboardResponse.TodayRevenue.builder()
        .todayRevenue(saleRepository.findTodayRevenue())
        .build();
  }

  public DashboardResponse.MonthlyRevenue getMonthlyRevenue() {
    return DashboardResponse.MonthlyRevenue.builder()
        .monthlyRevenue(saleRepository.findMonthlyRevenue())
        .build();
  }

  public List<DashboardResponse.StockEntry> getBooksInStock() {
    return bookCopyRepository.findAvailableCopiesPerBook().stream()
        .map(
            p ->
                DashboardResponse.StockEntry.builder()
                    .bookId(p.getBookId())
                    .title(p.getTitle())
                    .availableCopies(p.getAvailableCopies())
                    .build())
        .toList();
  }

  public List<DashboardResponse.StockEntry> getLowStock() {
    return bookCopyRepository.findLowStockBooks().stream()
        .map(
            p ->
                DashboardResponse.StockEntry.builder()
                    .bookId(p.getBookId())
                    .title(p.getTitle())
                    .availableCopies(p.getAvailableCopies())
                    .build())
        .toList();
  }

  public List<DashboardResponse.TopSellerEntry> getTopSellers(int limit) {
    return saleRepository.findTopSellers(PageRequest.of(0, limit)).stream()
        .map(
            p ->
                DashboardResponse.TopSellerEntry.builder()
                    .bookId(p.getBookId())
                    .title(p.getTitle())
                    .unitsSold(p.getTotalSold())
                    .build())
        .toList();
  }

  public List<DashboardResponse.RevenueByGenreEntry> getRevenueByGenre() {
    return saleRepository.findRevenueByGenre().stream()
        .map(
            p ->
                DashboardResponse.RevenueByGenreEntry.builder()
                    .genreId(p.getGenreId())
                    .genreName(p.getGenreName())
                    .revenue(p.getTotalRevenue())
                    .build())
        .toList();
  }
}
