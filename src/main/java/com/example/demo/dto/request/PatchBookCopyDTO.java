package com.example.demo.dto.request;

import com.example.demo.entity.enums.BookStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchBookCopyDTO {

  private BookStatus status;

  @Size(max = 100)
  private String location;
}
