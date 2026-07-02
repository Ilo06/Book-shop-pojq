package com.example.demo.service;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.entity.Sale;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        .todayRevenue(
            saleRepository.findTodaySale().stream()
                .map(this::getSaleRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .build();
  }

  public DashboardResponse.MonthlyRevenue getMonthlyRevenue() {
    return DashboardResponse.MonthlyRevenue.builder()
        .monthlyRevenue(
            saleRepository.findMonthlySale().stream()
                .map(this::getSaleRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .build();
  }

  public List<DashboardResponse.StockEntry> getBooksInStock() {
    return bookCopyRepository.getAllBooksStock().stream()
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
    Map<Map<UUID, String>, BigDecimal> genreRevenueMap = new HashMap<>();

    saleRepository.findAllConfirmedSale().stream()
        .flatMap(s -> s.getBooks().stream())
        .forEach(
            saleBookCopy ->
                saleBookCopy
                    .getBookCopy()
                    .getBook()
                    .getGenres()
                    .forEach(
                        genre -> {
                          if (genreRevenueMap.containsKey(Map.of(genre.getId(), genre.getName()))) {
                            genreRevenueMap.computeIfPresent(
                                Map.of(genre.getId(), genre.getName()),
                                (uuidStringMap, actualValue) ->
                                    actualValue.add(
                                        saleBookCopy
                                            .getBookCopy()
                                            .getPrice(saleBookCopy.getSale().getCreationDateTime())
                                            .multiply(
                                                BigDecimal.valueOf(saleBookCopy.getQuantity()))));
                          } else {
                            genreRevenueMap.put(
                                Map.of(genre.getId(), genre.getName()),
                                (saleBookCopy
                                    .getBookCopy()
                                    .getPrice(saleBookCopy.getSale().getCreationDateTime())
                                    .multiply(BigDecimal.valueOf(saleBookCopy.getQuantity()))));
                          }
                        }));

    return genreRevenueMap.keySet().stream()
        .flatMap(
            map ->
                map.keySet().stream()
                    .map(
                        genreId ->
                            DashboardResponse.RevenueByGenreEntry.builder()
                                .genreId(genreId)
                                .genreName(map.get(genreId))
                                .revenue(genreRevenueMap.get(Map.of(genreId, map.get(genreId))))
                                .build()))
        .toList();
  }

  private BigDecimal getSaleRevenue(Sale sale) {
    return BigDecimal.valueOf(
        sale.getBooks().stream()
            .mapToDouble(
                saleBookCopy ->
                    saleBookCopy.getQuantity()
                        * Double.parseDouble(
                            saleBookCopy
                                .getBookCopy()
                                .getPrice(sale.getCreationDateTime())
                                .toString()))
            .sum());
  }
}
