package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private SaleBookCopyRepository saleBookCopyRepository;
  @Mock private BookCopyRepository bookCopyRepository;

  @InjectMocks private SaleService saleService;

  @Test
  void findByDateBetween_returnsSales() {
    var from = LocalDate.now().minusDays(7);
    var to = LocalDate.now();
    var sale = new Sale();
    when(saleRepository.findBySaleDateBetween(from, to)).thenReturn(List.of(sale));

    var result = saleService.findByDateBetween(from, to);

    assertEquals(1, result.size());
  }

  @Test
  void findById_throwsWhenNotFound() {
    var id = UUID.randomUUID();
    when(saleRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> saleService.findById(id));
  }

  @Test
  void save_createsSale() {
    var bookCopyId = UUID.randomUUID();
    var input =
        new com.example.demo.dto.request.CreateSaleDTO(LocalDate.now(), List.of(bookCopyId));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(new BookCopy()));
    when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(saleBookCopyRepository.saveAll(any())).thenReturn(List.of());

    var result = saleService.save(input);

    assertNotNull(result);
  }
}
