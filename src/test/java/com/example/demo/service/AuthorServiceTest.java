package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Author;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class AuthorServiceTest {

  private AuthorRepository authorRepository;
  private AuthorService authorService;

  private UUID authorId;
  private Author author;
  private CreateAuthorDTO createAuthorDTO;

  @BeforeEach
  void setUp() {
    authorRepository = mock(AuthorRepository.class);
    authorService = new AuthorService(authorRepository);

    authorId = UUID.randomUUID();
    author = Author.builder().id(authorId).firstName("John").lastName("Doe").build();

    createAuthorDTO = new CreateAuthorDTO("Jane", "Smith");
  }

  @Test
  void findAll_shouldReturnPageOfAuthorResponse() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<Author> authorPage = new PageImpl<>(List.of(author));
    when(authorRepository.findAll(pageable)).thenReturn(authorPage);

    PageResponse<AuthorResponse> result = authorService.findAll(pageable);

    assertEquals(1, result.getData().size());
    assertEquals(authorId, result.getData().getFirst().getId());
    assertEquals("John", result.getData().getFirst().getFirstName());
    assertEquals("Doe", result.getData().getFirst().getLastName());
  }

  @Test
  void findById_shouldReturnAuthorResponse() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    AuthorResponse result = authorService.findById(authorId);

    assertEquals(authorId, result.getId());
    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
  }

  @Test
  void findById_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authorService.findById(id));
  }

  @Test
  void create_shouldReturnAuthorResponse() {
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
            createAuthorDTO.getFirstName(), createAuthorDTO.getLastName()))
        .thenReturn(false);
    when(authorRepository.save(any(Author.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AuthorResponse result = authorService.create(createAuthorDTO);

    assertEquals(createAuthorDTO.getFirstName(), result.getFirstName());
    assertEquals(createAuthorDTO.getLastName(), result.getLastName());
  }

  @Test
  void create_shouldThrowResourceConflictExceptionWhenNameExists() {
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(
            createAuthorDTO.getFirstName(), createAuthorDTO.getLastName()))
        .thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> authorService.create(createAuthorDTO));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void update_shouldReturnUpdatedAuthorResponse() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Jane", "Smith"))
        .thenReturn(false);
    when(authorRepository.save(any(Author.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AuthorResponse result = authorService.update(authorId, createAuthorDTO);

    assertEquals("Jane", result.getFirstName());
    assertEquals("Smith", result.getLastName());
  }

  @Test
  void update_shouldThrowResourceConflictExceptionWhenNameTaken() {
    CreateAuthorDTO input = new CreateAuthorDTO("Existing", "Name");
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Existing", "Name"))
        .thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> authorService.update(authorId, input));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void update_shouldNotThrowConflictWhenNameUnchanged() {
    CreateAuthorDTO input = new CreateAuthorDTO("John", "Doe");
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe"))
        .thenReturn(true);
    when(authorRepository.save(any(Author.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AuthorResponse result = authorService.update(authorId, input);

    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
  }

  @Test
  void delete_shouldDeleteAuthor() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    authorService.delete(authorId);

    verify(authorRepository).delete(author);
  }

  @Test
  void delete_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authorService.delete(id));
    verify(authorRepository, never()).delete(any());
  }

  @Test
  void getOrThrow_shouldReturnAuthor() {
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

    Author result = authorService.getOrThrow(authorId);

    assertEquals(authorId, result.getId());
    assertEquals("John", result.getFirstName());
  }

  @Test
  void getOrThrow_shouldThrowResourceNotFoundException() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authorService.getOrThrow(id));
  }
}
