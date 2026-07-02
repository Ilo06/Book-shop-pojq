package com.example.demo.service;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.SaleBookCopyResponse;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.entity.keys.SaleBookCopyId;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  public SaleResponse save(@Valid CreateSaleDTO sale) {
    sale.getBookCopyIds()
        .forEach(
            bookCopyId -> {
              if (bookCopyRepository.findById(bookCopyId).isEmpty()) {
                throw new ResourceNotFoundException(
                    "Book copy with id: " + bookCopyId + " not found");
              }
            });

    Sale saleToSave = Sale.builder().creationDateTime(sale.getCreationDateTime()).build();
    Sale newSale = saleRepository.save(saleToSave);

    List<SaleBookCopy> saleItems =
        sale.getBookCopyIds().stream()
            .map(bookCopyId -> createSaleBookCopy(newSale, bookCopyId))
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

  private SaleBookCopy createSaleBookCopy(Sale sale, UUID bookCopyId) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(bookCopyId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Book copy with id: " + bookCopyId + " not found"));

    return SaleBookCopy.builder()
        .saleBookCopyId(new SaleBookCopyId(sale.getId(), bookCopy.getId()))
        .sale(sale)
        .bookCopy(bookCopy)
        .build();
  }

  private SaleResponse toResponse(Sale sale) {
    List<SaleBookCopyResponse> bookResponses =
        sale.getBooks().stream()
            .map(
                saleBookCopy ->
                    SaleBookCopyResponse.builder()
                        .bookCopyId(saleBookCopy.getBookCopy().getId())
                        .price(saleBookCopy.getBookCopy().getPrice())
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
