package com.example.demo.service;

import com.example.demo.dto.request.CreateSaleBookCopyDTO;
import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.SaleBookCopyResponse;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.time.LocalDate;
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

  public List<SaleResponse> findByDateBetween(LocalDate from, LocalDate to) {
    if (from == null) from = LocalDate.of(1970, 1, 1);
    if (to == null) to = LocalDate.now();
    return saleRepository.findBySaleDateBetween(from, to).stream().map(this::toResponse).toList();
  }

  @Transactional
  public SaleResponse save(@Valid CreateSaleDTO sale) {
    sale.getBooks()
        .forEach(
            bookCopy -> {
              if (bookCopyRepository.findById(bookCopy.getBookCopyId()).isEmpty()) {
                throw new ResourceNotFoundException(
                    "Book copy with id: " + bookCopy.getBookCopyId() + " not found");
              }
              if (saleBookCopyRepository.existsByBookCopyId(bookCopy.getBookCopyId())) {
                throw new ResourceConflictException(
                    "Book copy with id: " + bookCopy.getBookCopyId() + " already sold");
              }
            });

    Sale saleToSave = Sale.builder().saleDate(sale.getSaleDate()).build();
    Sale newSale = saleRepository.save(saleToSave);

    List<SaleBookCopy> saleItems =
        sale.getBooks().stream().map(dto -> createSaleBookCopy(newSale, dto)).toList();

    List<SaleBookCopy> savedItems = saleBookCopyRepository.saveAll(saleItems);
    newSale.setBooks(savedItems);
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

  private SaleBookCopy createSaleBookCopy(Sale sale, CreateSaleBookCopyDTO saleBookCopyDTO) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(saleBookCopyDTO.getBookCopyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Book copy with id: " + saleBookCopyDTO.getBookCopyId() + " not found"));

    return SaleBookCopy.builder()
        .saleBookCopyId(new SaleBookCopyId(sale.getId(), bookCopy.getId()))
        .sale(sale)
        .bookCopy(bookCopy)
        .price(saleBookCopyDTO.getPrice())
        .build();
  }

  private SaleResponse toResponse(Sale sale) {
    List<SaleBookCopyResponse> bookResponses =
        sale.getBooks().stream()
            .map(
                saleBookCopy ->
                    SaleBookCopyResponse.builder()
                        .bookCopyId(saleBookCopy.getBookCopy().getId())
                        .price(saleBookCopy.getPrice())
                        .build())
            .toList();

    return SaleResponse.builder()
        .id(sale.getId())
        .saleDate(sale.getSaleDate())
        .books(bookResponses)
        .build();
  }
}
