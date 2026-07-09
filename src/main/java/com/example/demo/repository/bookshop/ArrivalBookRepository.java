package com.example.demo.repository.bookshop;

import com.example.demo.entity.ArrivalBook;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalBookRepository extends JpaRepository<ArrivalBook, UUID> {

  List<ArrivalBook> findByArrivalId(UUID arrivalId);
}
