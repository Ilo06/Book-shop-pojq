package com.example.demo.repository.bookshop;

import com.example.demo.entity.BookCopy;
import com.example.demo.entity.BookCopyPrice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyPriceRepository extends JpaRepository<BookCopyPrice, UUID> {
  void deleteBookCopyPriceByBookCopy(BookCopy bookCopy);
}
