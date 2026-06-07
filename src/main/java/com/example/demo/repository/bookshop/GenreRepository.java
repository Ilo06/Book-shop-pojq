package com.example.demo.repository.bookshop;

import com.example.demo.entity.Genre;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {

  boolean existsByNameIgnoreCase(String name);

  Optional<Genre> findByNameIgnoreCase(String name);

  Page<Genre> findAll(Pageable pageable);
}
