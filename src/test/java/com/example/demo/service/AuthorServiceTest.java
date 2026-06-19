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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock AuthorRepository authorRepository;
  AuthorService service;
  Author author;
  UUID id;

  @BeforeEach
  void setUp() {
    service = new AuthorService(authorRepository);
    id = UUID.randomUUID();
    author = Author.builder().id(id).firstName("John").lastName("Doe").build();
  }

  @Test
  void findAll_returnsPage() {
    Pageable pageable = PageRequest.of(0, 20);
    Page<Author> page = new PageImpl<>(List.of(author), pageable, 1);
    when(authorRepository.findAll(pageable)).thenReturn(page);

    PageResponse<AuthorResponse> result = service.findAll(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals("John", result.getData().get(0).getFirstName());
  }

  @Test
  void findById_returnsAuthor() {
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));

    AuthorResponse result = service.findById(id);

    assertEquals("John", result.getFirstName());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
  }

  @Test
  void create_savesAuthor() {
    CreateAuthorDTO input = new CreateAuthorDTO("Jane", "Smith");
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Jane", "Smith"))
        .thenReturn(false);
    when(authorRepository.save(any()))
        .thenReturn(Author.builder().id(UUID.randomUUID()).firstName("Jane").lastName("Smith").build());

    AuthorResponse result = service.create(input);

    assertEquals("Jane", result.getFirstName());
    assertEquals("Smith", result.getLastName());
  }

  @Test
  void create_throwsOnDuplicate() {
    CreateAuthorDTO input = new CreateAuthorDTO("John", "Doe");
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe"))
        .thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> service.create(input));
  }

  @Test
  void update_changesAuthor() {
    Author existing = Author.builder().id(id).firstName("Old").lastName("Name").build();
    when(authorRepository.findById(id)).thenReturn(Optional.of(existing));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("New", "Name"))
        .thenReturn(false);
    when(authorRepository.save(any())).thenReturn(existing);

    AuthorResponse result = service.update(id, new CreateAuthorDTO("New", "Name"));

    assertEquals("New", result.getFirstName());
  }

  @Test
  void update_sameNameDoesNotConflict() {
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("John", "Doe"))
        .thenReturn(true);
    when(authorRepository.save(any())).thenReturn(author);

    AuthorResponse result = service.update(id, new CreateAuthorDTO("John", "Doe"));

    assertEquals("John", result.getFirstName());
  }

  @Test
  void update_throwsWhenNotFound() {
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> service.update(id, new CreateAuthorDTO("New", "Name")));
  }

  @Test
  void delete_removesAuthor() {
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));

    service.delete(id);

    verify(authorRepository).delete(author);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
  }

  @Test
  void getOrThrow_returnsAuthor() {
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));

    Author result = service.getOrThrow(id);

    assertEquals(id, result.getId());
  }

  @Test
  void getOrThrow_throwsWhenNotFound() {
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getOrThrow(id));
  }
}
