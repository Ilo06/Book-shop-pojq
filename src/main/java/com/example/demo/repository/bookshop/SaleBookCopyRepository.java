package com.example.demo.repository.bookshop;

import com.example.demo.entity.SaleBookCopy;
import com.example.demo.entity.keys.SaleBookCopyId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleBookCopyRepository extends JpaRepository<SaleBookCopy, SaleBookCopyId> {

  List<SaleBookCopy> findBySaleId(UUID saleId);

  boolean existsByBookCopyId(UUID bookCopyId);
}
