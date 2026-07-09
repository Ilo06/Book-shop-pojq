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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class GenreServiceTest {

  private GenreRepository genreRepository;
  private GenreService genreService;

  private UUID genreId;
  private Genre genre;
  private CreateGenreDTO createGenreDTO;

  @BeforeEach
  void setUp() {
    genreRepository = mock(GenreRepository.class);
    genreService = new GenreService(genreRepository);

    genreId = UUID.randomUUID();
    genre = Genre.builder().id(genreId).name("Fiction").build();
    createGenreDTO = new CreateGenreDTO("Science-Fiction");
  }

  @Test
  void findAll_shouldReturnPageOfGenreResponse() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<Genre> genrePage = new PageImpl<>(List.of(genre));
    when(genreRepository.findAll(pageable)).thenReturn(genrePage);

    PageResponse<GenreResponse> result = genreService.findAll(pageable);

    assertEquals(1, result.getData().size());
    assertEquals(genreId, result.getData().getFirst().getId());
    assertEquals("Fiction", result.getData().getFirst().getName());
  }

  @Test
  void findById_shouldReturnGenreResponse() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    GenreResponse result = genreService.findById(genreId);

    assertEquals(genreId, result.getId());
    assertEquals("Fiction", result.getName());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> genreService.findById(id));
  }

  @Test
  void create_shouldReturnGenreResponse() {
    when(genreRepository.existsByNameIgnoreCase(createGenreDTO.getName())).thenReturn(false);
    when(genreRepository.save(any(Genre.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    GenreResponse result = genreService.create(createGenreDTO);

    assertEquals(createGenreDTO.getName(), result.getName());
  }

  @Test
  void create_shouldThrowResourceConflictExceptionWhenNameExists() {
    when(genreRepository.existsByNameIgnoreCase(createGenreDTO.getName())).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> genreService.create(createGenreDTO));
    verify(genreRepository, never()).save(any());
  }

  @Test
  void update_shouldReturnUpdatedGenreResponse() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Science-Fiction")).thenReturn(false);
    when(genreRepository.save(any(Genre.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    GenreResponse result = genreService.update(genreId, createGenreDTO);

    assertEquals("Science-Fiction", result.getName());
  }

  @Test
  void update_shouldThrowResourceConflictExceptionWhenNameTaken() {
    CreateGenreDTO input = new CreateGenreDTO("Horror");
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.existsByNameIgnoreCase("Horror")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> genreService.update(genreId, input));
    verify(genreRepository, never()).save(any());
  }

  @Test
  void update_shouldNotThrowConflictWhenNameUnchanged() {
    CreateGenreDTO input = new CreateGenreDTO("Fiction");
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.save(any(Genre.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    GenreResponse result = genreService.update(genreId, input);

    assertEquals("Fiction", result.getName());
    verify(genreRepository, never()).existsByNameIgnoreCase(any());
  }

  @Test
  void delete_shouldDeleteGenre() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    genreService.delete(genreId);

    verify(genreRepository).delete(genre);
    verify(genreRepository).flush();
  }

  @Test
  void delete_shouldThrowResourceConflictExceptionWhenReferenced() {
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    doThrow(DataIntegrityViolationException.class).when(genreRepository).flush();

    assertThrows(ResourceConflictException.class, () -> genreService.delete(genreId));
    verify(genreRepository).delete(genre);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(genreRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> genreService.delete(id));
    verify(genreRepository, never()).delete(any());
  }
}
