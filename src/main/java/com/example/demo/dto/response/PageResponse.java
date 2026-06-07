package com.example.demo.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

  private List<T> data;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean last;

  public static <T> PageResponse<T> of(Page<T> springPage) {
    return PageResponse.<T>builder()
        .data(springPage.getContent())
        .page(springPage.getNumber())
        .size(springPage.getSize())
        .totalElements(springPage.getTotalElements())
        .totalPages(springPage.getTotalPages())
        .last(springPage.isLast())
        .build();
  }
}
