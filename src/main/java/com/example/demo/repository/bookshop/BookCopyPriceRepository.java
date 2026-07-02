package com.example.demo.repository.bookshop;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.BookCopyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookCopyPriceRepository extends JpaRepository<BookCopyPrice, UUID> {
    void deleteBookCopyPriceByBookCopy(BookCopy bookCopy);
}
