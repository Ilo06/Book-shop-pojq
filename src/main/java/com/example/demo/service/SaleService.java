package com.example.demo.service;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.RevenueResponse;
import com.example.demo.dto.response.SaleBookCopyResponse;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.entity.enums.SaleStatus;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleService {
  private final SaleRepository saleRepository;
  private final SaleBookCopyRepository saleBookCopyRepository;
  private final BookCopyRepository bookCopyRepository;

  public List<SaleResponse> findByDateBetween(Instant from, Instant to) {
    if (from == null) from = LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
    if (to == null) to = Instant.now();
    return saleRepository.findByCreationDateTimeBetween(from, to).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public SaleResponse save(@Valid CreateSaleDTO sale) throws BadRequestException {
    Map<UUID, QuantifiedBookCopyDTO> qdcMap =
        sale.getQuantifiedBookCopyList().stream()
            .collect(
                Collectors.toMap(
                    QuantifiedBookCopyDTO::getBookCopyId,
                    qbc -> qbc,
                    (qbc, qbc2) ->
                        new QuantifiedBookCopyDTO(
                            qbc.getBookCopyId(), qbc.getQuantity() + qbc2.getQuantity())));

    for (UUID qbcId : qdcMap.keySet()) {
      if (bookCopyRepository.findById(qbcId).isEmpty()) {
        throw new ResourceNotFoundException("Book copy with id: " + qbcId + " not found");
      }
      if ((bookCopyRepository.getBookCopyStockByCopyId(qbcId) - qdcMap.get(qbcId).getQuantity())
          < 0) {
        throw new BadRequestException("Requested amount exceed remaining stock");
      }
    }

    Sale saleToSave =
        Sale.builder()
            .isReservation(sale.getIsReservation())
            .creationDateTime(sale.getCreationDateTime())
            .build();
    Sale newSale = saleRepository.save(saleToSave);

    List<SaleBookCopy> saleItems =
        sale.getQuantifiedBookCopyList().stream()
            .map(qbc -> createSaleBookCopy(newSale, qbc.getBookCopyId(), qbc.getQuantity()))
            .toList();

    newSale.setBooks(saleBookCopyRepository.saveAll(saleItems));
    return toResponse(newSale);
  }

  public SaleResponse findById(UUID id) {
    Optional<Sale> optionalSale = saleRepository.findById(id);
    if (optionalSale.isEmpty()) {
      throw new ResourceNotFoundException("Sale with id " + id + " not found");
    }
    Sale sale = optionalSale.get();
    sale.setBooks(saleBookCopyRepository.findBySaleId(sale.getId()));
    return toResponse(sale);
  }

  public SaleResponse finalize(UUID id, boolean confirm) {
    Sale sale =
        saleRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " not found"));
    sale.setFinalizationDateTime(Instant.now());
    sale.setStatus(confirm ? SaleStatus.CONFIRMED : SaleStatus.REJECTED);
    return toResponse(saleRepository.save(sale));
  }

  public RevenueResponse.TodayRevenue getTodayRevenue() {
    return RevenueResponse.TodayRevenue.builder()
        .todayRevenue(
            saleRepository.findTodaySale().stream()
                .map(this::getSaleRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .build();
  }

  public RevenueResponse.MonthlyRevenue getMonthlyRevenue() {
    return RevenueResponse.MonthlyRevenue.builder()
        .monthlyRevenue(
            saleRepository.findMonthlySale().stream()
                .map(this::getSaleRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .build();
  }

  public List<RevenueResponse.RevenueByGenreEntry> getRevenueByGenre() {
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
                            RevenueResponse.RevenueByGenreEntry.builder()
                                .genreId(genreId)
                                .genreName(map.get(genreId))
                                .revenue(genreRevenueMap.get(Map.of(genreId, map.get(genreId))))
                                .build()))
        .sorted(Comparator.comparing(RevenueResponse.RevenueByGenreEntry::getRevenue))
        .toList()
        .reversed();
  }

  public List<RevenueResponse.TopSellerEntry> getTopSellers(int limit) {
    return saleRepository.findTopSellers(limit).stream()
        .map(
            p ->
                RevenueResponse.TopSellerEntry.builder()
                    .bookId(p.getBookId())
                    .title(p.getTitle())
                    .unitsSold(p.getTotalSold())
                    .build())
        .toList();
  }

  private SaleBookCopy createSaleBookCopy(Sale sale, UUID bookCopyId, int quantity) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(bookCopyId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Book copy with id: " + bookCopyId + " not found"));

    return SaleBookCopy.builder().sale(sale).bookCopy(bookCopy).quantity(quantity).build();
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

  private SaleResponse toResponse(Sale sale) {
    List<SaleBookCopyResponse> bookResponses =
        sale.getBooks().stream()
            .map(
                saleBookCopy ->
                    SaleBookCopyResponse.builder()
                        .bookCopyId(saleBookCopy.getBookCopy().getId())
                        .price(saleBookCopy.getBookCopy().getPrice(sale.getCreationDateTime()))
                        .quantity(saleBookCopy.getQuantity())
                        .build())
            .toList();

    return SaleResponse.builder()
        .id(sale.getId())
        .creationDateTime(sale.getCreationDateTime())
        .saleStatus(sale.getStatus())
        .isReservation(sale.getIsReservation())
        .finalizationDateTime(sale.getFinalizationDateTime())
        .books(bookResponses)
        .build();
  }
}
