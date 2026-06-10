package com.example.demo.dto.response;

import java.util.UUID;

import com.example.demo.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {
  private UUID id;
  private String firstName;
  private String lastName;

  public static AuthorResponse from(Author author) {
    return AuthorResponse.builder()
            .id(author.getId())
            .firstName(author.getFirstName())
            .lastName(author.getLastName())
            .build();
  }
}
