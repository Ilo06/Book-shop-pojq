package com.example.demo.service;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.GenreRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreService {

  private final GenreRepository genreRepository;

  public List<GenreResponse> findAll() {
    return genreRepository.findAll().stream().map(this::toResponse).toList();
  }

  public GenreResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public GenreResponse create(CreateGenreDTO createGenreDTO) {
    if (genreRepository.existsByNameIgnoreCase(createGenreDTO.getName())) {
      throw new ResourceConflictException(
          "Genre with name '" + createGenreDTO.getName() + "' already exists");
    }
    Genre genre = Genre.builder().name(createGenreDTO.getName()).build();
    return toResponse(genreRepository.save(genre));
  }

  @Transactional
  public GenreResponse update(UUID id, CreateGenreDTO createGenreDTO) {
    Genre genre = getOrThrow(id);
    if (!genre.getName().equalsIgnoreCase(createGenreDTO.getName())
        && genreRepository.existsByNameIgnoreCase(createGenreDTO.getName())) {
      throw new ResourceConflictException(
          "Genre with name '" + createGenreDTO.getName() + "' already exists");
    }
    genre.setName(createGenreDTO.getName());
    return toResponse(genreRepository.save(genre));
  }

  @Transactional
  public void delete(UUID id) {
    Genre genre = getOrThrow(id);
    if (genre.getBooks() != null && !genre.getBooks().isEmpty()) {
      throw new ResourceConflictException("Cannot delete genre with associated books");
    }
    genreRepository.delete(genre);
  }

  private Genre getOrThrow(UUID id) {
    return genreRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));
  }

  private GenreResponse toResponse(Genre genre) {
    return GenreResponse.builder().id(genre.getId()).name(genre.getName()).build();
  }
}
