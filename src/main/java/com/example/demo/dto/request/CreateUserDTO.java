package com.example.demo.dto.request;

import com.example.demo.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
  @NotBlank
  @Size(min = 1, max = 255)
  private String username;

  @NotBlank private String password;

  private UserRole role;
}
