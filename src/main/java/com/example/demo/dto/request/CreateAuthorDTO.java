package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthorDTO {
  @NotBlank
  @Size(min = 1, max = 100)
  private String firstName;

  @NotBlank
  @Size(min = 1, max = 100)
  private String lastName;
}
