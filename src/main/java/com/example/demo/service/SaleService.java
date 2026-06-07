package com.example.demo.service;

import com.example.demo.entity.Sale;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import jakarta.transaction.Transactional;
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

  public List<Sale> findByDateBetween(LocalDate from, LocalDate to) {
    return saleRepository.findBySaleDateBetween(from, to);
  }

  public List<Sale> getAll() {
    return saleRepository.findAll();
  }

  @Transactional
  public Sale save(Sale sale) {
    sale.getBooks()
        .forEach(
            bookCopy -> {
              if (saleBookCopyRepository.existsByBookCopyId(((bookCopy.getBookCopy().getId())))) {
                throw new ResourceConflictException("Book copy has already been sold");
              }
            });
    saleRepository.save(sale);
    saleBookCopyRepository.saveAll(sale.getBooks());
    return sale;
  }

  public Sale findById(UUID id) {
    Optional<Sale> optionalSale = saleRepository.findById(id);
    if (optionalSale.isEmpty()) {
      throw new ResourceNotFoundException("Sale with id " + id + " not found");
    }
    Sale sale = optionalSale.get();
    sale.setBooks(saleBookCopyRepository.findBySaleId(sale.getId()));
    return sale;
  }
}
