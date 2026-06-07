package com.example.demo.service;

import com.example.demo.dto.request.CreateAuthorDTO;
import com.example.demo.dto.response.AuthorResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Author;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.AuthorRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final AuthorRepository authorRepository;

  public PageResponse<AuthorResponse> findAll(Pageable pageable) {
    return PageResponse.of(authorRepository.findAll(pageable).map(this::toResponse));
  }

  public AuthorResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public AuthorResponse create(CreateAuthorDTO input) {
    checkNameUniqueness(null, input.getFirstName(), input.getLastName());
    Author author =
        Author.builder().firstName(input.getFirstName()).lastName(input.getLastName()).build();
    return toResponse(authorRepository.save(author));
  }

  @Transactional
  public AuthorResponse update(UUID id, CreateAuthorDTO input) {
    Author author = getOrThrow(id);
    checkNameUniqueness(id, input.getFirstName(), input.getLastName());
    author.setFirstName(input.getFirstName());
    author.setLastName(input.getLastName());
    return toResponse(authorRepository.save(author));
  }

  @Transactional
  public void delete(UUID id) {
    Author author = getOrThrow(id);
    authorRepository.delete(author);
  }

  public Author getOrThrow(UUID id) {
    return authorRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
  }

  private void checkNameUniqueness(UUID excludeId, String firstName, String lastName) {
    if (!authorRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName)) {
      return;
    }
    if (excludeId != null) {
      authorRepository
          .findById(excludeId)
          .ifPresent(
              existing -> {
                boolean sameRecord =
                    existing.getFirstName().equalsIgnoreCase(firstName)
                        && existing.getLastName().equalsIgnoreCase(lastName);
                if (sameRecord) {
                  return;
                }
                throw new ResourceConflictException(
                    "Author with name '" + firstName + " " + lastName + "' already exists");
              });
      return;
    }
    throw new ResourceConflictException(
        "Author with name '" + firstName + " " + lastName + "' already exists");
  }

  private AuthorResponse toResponse(Author author) {
    return AuthorResponse.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .build();
  }
}
