package com.example.demo.repository.bookshop;

import com.example.demo.entity.Genre;
// import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
  // Optional<Genre> findByName(String name);

  boolean existsByName(String name);
}
