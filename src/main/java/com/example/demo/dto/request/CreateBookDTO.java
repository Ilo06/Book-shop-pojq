package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookDTO {

  @NotBlank
  @Size(min = 1, max = 255)
  private String title;

  @NotBlank
  @Pattern(regexp = "^(97[89])?[0-9]{9}[0-9X]$|^[0-9]{13}$", message = "Invalid ISBN format")
  private String isbn;

  private String description;

  @NotNull private LocalDate publishDate;

  @NotNull private UUID genreId;

  @NotNull private List<UUID> authorIds;
}
