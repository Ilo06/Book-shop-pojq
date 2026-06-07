package com.example.demo.repository.bookshop;

import com.example.demo.entity.Author;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

  boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);

  Page<Author> findAll(Pageable pageable);
}
