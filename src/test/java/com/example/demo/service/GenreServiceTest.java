package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.dto.response.GenreResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.GenreRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock GenreRepository genreRepository;
  GenreService service;
  Genre genre;
  UUID id;

  @BeforeEach
  void setUp() {
    service = new GenreService(genreRepository);
    id = UUID.randomUUID();
    genre = Genre.builder().id(id).name("Fiction").build();
  }

  @Test
  void findAll_returnsPage() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<Genre> page = new PageImpl<>(List.of(genre), pageable, 1);
    when(genreRepository.findAll(pageable)).thenReturn(page);

    PageResponse<GenreResponse> result = service.findAll(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("Fiction", result.getData().get(0).getName());
  }

  @Test
  void findById_returnsGenre() {
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

    GenreResponse result = service.findById(id);

    assertEquals("Fiction", result.getName());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
  }

  @Test
  void create_savesGenre() {
    CreateGenreDTO input = new CreateGenreDTO("Non-Fiction");
    when(genreRepository.existsByNameIgnoreCase("Non-Fiction")).thenReturn(false);
    when(genreRepository.save(any()))
        .thenReturn(Genre.builder().id(UUID.randomUUID()).name("Non-Fiction").build());

    GenreResponse result = service.create(input);

    assertEquals("Non-Fiction", result.getName());
  }

  @Test
  void create_throwsOnDuplicate() {
    CreateGenreDTO input = new CreateGenreDTO("Fiction");
    when(genreRepository.existsByNameIgnoreCase("Fiction")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> service.create(input));
  }

  @Test
  void update_changesGenre() {
    CreateGenreDTO input = new CreateGenreDTO("Science");
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Science")).thenReturn(false);
    when(genreRepository.save(any())).thenReturn(genre);

    GenreResponse result = service.update(id, input);

    assertEquals("Science", result.getName());
  }

  @Test
  void update_throwsOnDuplicate() {
    CreateGenreDTO input = new CreateGenreDTO("Romance");
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Romance")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> service.update(id, input));
  }

  @Test
  void update_sameNameDoesNotConflict() {
    CreateGenreDTO input = new CreateGenreDTO("Fiction");
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    when(genreRepository.save(any())).thenReturn(genre);

    GenreResponse result = service.update(id, input);

    assertEquals("Fiction", result.getName());
  }

  @Test
  void delete_removesGenre() {
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

    service.delete(id);

    verify(genreRepository).delete(genre);
  }

  @Test
  void delete_throwsWhenReferenced() {
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
    doThrow(DataIntegrityViolationException.class).when(genreRepository).flush();

    assertThrows(ResourceConflictException.class, () -> service.delete(id));
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
  }
}
