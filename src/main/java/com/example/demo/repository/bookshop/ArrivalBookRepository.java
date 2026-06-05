package com.example.demo.repository.bookshop;

import com.example.demo.entity.ArrivalBook;
import com.example.demo.entity.keys.ArrivalBookId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalBookRepository extends JpaRepository<ArrivalBook, ArrivalBookId> {

  List<ArrivalBook> findByArrivalId(UUID arrivalId);
}
