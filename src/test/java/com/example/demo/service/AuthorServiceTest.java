package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.entity.Author;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock private AuthorRepository authorRepository;

  @InjectMocks private AuthorService authorService;

  @Test
  void findAll_returnsPagedAuthors() {
    var author = Author.builder().id(UUID.randomUUID()).firstName("John").lastName("Doe").build();
    var page = new PageImpl<>(java.util.List.of(author));
    when(authorRepository.findAll(any(Pageable.class))).thenReturn(page);

    var result = authorService.findAll(PageRequest.of(0, 20));

    assertEquals(1, result.getData().size());
    assertEquals("John", result.getData().get(0).getFirstName());
  }

  @Test
  void findById_returnsAuthor() {
    var id = UUID.randomUUID();
    var author = Author.builder().id(id).firstName("Jane").lastName("Doe").build();
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));

    var result = authorService.findById(id);

    assertEquals("Jane", result.getFirstName());
  }

  @Test
  void findById_throwsWhenNotFound() {
    var id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> authorService.findById(id));
  }

  @Test
  void create_savesAuthor() {
    var input = new CreateAuthorDTO("Alice", "Smith");
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Alice", "Smith"))
        .thenReturn(false);
    when(authorRepository.save(any(Author.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = authorService.create(input);

    assertEquals("Alice", result.getFirstName());
    assertEquals("Smith", result.getLastName());
  }

  @Test
  void create_throwsWhenDuplicate() {
    var input = new CreateAuthorDTO("Alice", "Smith");
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("Alice", "Smith"))
        .thenReturn(true);

    assertThrows(ResourceConflictException.class, () -> authorService.create(input));
  }

  @Test
  void update_updatesAuthor() {
    var id = UUID.randomUUID();
    var existing = Author.builder().id(id).firstName("Old").lastName("Name").build();
    var input = new CreateAuthorDTO("New", "Name");
    when(authorRepository.findById(id)).thenReturn(Optional.of(existing));
    when(authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("New", "Name"))
        .thenReturn(false);
    when(authorRepository.save(any(Author.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = authorService.update(id, input);

    assertEquals("New", result.getFirstName());
    assertEquals("Name", result.getLastName());
  }

  @Test
  void delete_removesAuthor() {
    var id = UUID.randomUUID();
    var author = Author.builder().id(id).firstName("Bob").lastName("Brown").build();
    when(authorRepository.findById(id)).thenReturn(Optional.of(author));
    doNothing().when(authorRepository).delete(author);

    authorService.delete(id);

    verify(authorRepository).delete(author);
  }
}
