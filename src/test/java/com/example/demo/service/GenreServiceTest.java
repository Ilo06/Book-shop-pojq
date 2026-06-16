package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateGenreDTO;
import com.example.demo.entity.Genre;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.GenreRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;

  @InjectMocks private GenreService genreService;

  @Test
  void findAll_returnsPagedGenres() {
    var genre = Genre.builder().id(UUID.randomUUID()).name("Fiction").build();
    var page = new PageImpl<>(java.util.List.of(genre));
    when(genreRepository.findAll(any(Pageable.class))).thenReturn(page);

    var result = genreService.findAll(PageRequest.of(0, 20));

    assertEquals(1, result.getData().size());
    assertEquals("Fiction", result.getData().get(0).getName());
  }

  @Test
  void findById_returnsGenre() {
    var id = UUID.randomUUID();
    var genre = Genre.builder().id(id).name("Non-Fiction").build();
    when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

    var result = genreService.findById(id);

    assertEquals("Non-Fiction", result.getName());
  }

  @Test
  void create_savesGenre() {
    var input = new CreateGenreDTO("Science");
    when(genreRepository.existsByNameIgnoreCase("Science")).thenReturn(false);
    when(genreRepository.save(any(Genre.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = genreService.create(input);

    assertEquals("Science", result.getName());
  }

  @Test
  void create_throwsWhenDuplicate() {
    var input = new CreateGenreDTO("Science");
    when(genreRepository.existsByNameIgnoreCase("Science")).thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> genreService.create(input));
  }

  @Test
  void update_updatesGenre() {
    var id = UUID.randomUUID();
    var existing = Genre.builder().id(id).name("Old").build();
    var input = new CreateGenreDTO("New");
    when(genreRepository.findById(id)).thenReturn(Optional.of(existing));
    when(genreRepository.existsByNameIgnoreCase("New")).thenReturn(false);
    when(genreRepository.save(any(Genre.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = genreService.update(id, input);

    assertEquals("New", result.getName());
  }
}
