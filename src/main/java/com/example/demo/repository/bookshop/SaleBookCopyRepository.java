package com.example.demo.repository.bookshop;

import com.example.demo.entity.SaleBookCopy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleBookCopyRepository extends JpaRepository<SaleBookCopy, UUID> {

  List<SaleBookCopy> findBySaleId(UUID saleId);

  boolean existsByBookCopyId(UUID bookCopyId);
}
