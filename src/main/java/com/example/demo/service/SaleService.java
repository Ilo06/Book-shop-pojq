package com.example.demo.service;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Sale;
import com.example.demo.entity.SaleBookCopy;
import com.example.demo.repository.bookshop.SaleBookCopyRepository;
import com.example.demo.repository.bookshop.SaleRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {
    private SaleRepository saleRepository;
    private SaleBookCopyRepository saleBookCopyRepository;

    public List<Sale> findByDateBetween(LocalDate from, LocalDate to) {
        return saleRepository.findBySaleDateBetween(from, to);
    }

    public Sale save(Sale sale) {
        sale.getBooks().forEach(bookCopy -> {
            if (saleBookCopyRepository.existsByBookCopyId(((bookCopy.getBookCopy().getId())))) {
                throw new BadRequestException("Book copy has already been sold");
            }
        });
        saleRepository.save(sale);
        saleBookCopyRepository.saveAll(sale.getBooks());
        return sale;
    }

    public Sale findById(UUID id) {
        Optional<Sale> optionalSale = saleRepository.findById(id);
        if (optionalSale.isEmpty()) {
            throw new NotFoundException("Sale with id " + id + " not found");
        }
        Sale sale = optionalSale.get();
        sale.setBooks(saleBookCopyRepository.findBySaleId(sale.getId()));
        return sale;
    }
}
