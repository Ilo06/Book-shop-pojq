package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.response.ExternalSearchResponse;
import com.example.demo.endpoint.rest.controller.bookshop.ExternalSearchController;
import com.example.demo.exception.BadGatewayException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ExternalSearchService;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ExternalSearchController.class, GlobalExceptionHandler.class})
class ExternalSearchControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ExternalSearchService externalSearchService;

  @Test
  void externalSearch_shouldReturn200() throws Exception {
    ExternalSearchResponse response =
        ExternalSearchResponse.builder()
            .provider("OpenLibrary")
            .isbn("9780451524935")
            .title("1984")
            .firstPublishYear(Year.of(1949))
            .authors(List.of("George Orwell"))
            .build();

    when(externalSearchService.findBookByISBN("9780451524935")).thenReturn(response);

    mockMvc
        .perform(
            get("/external-search")
                .param("isbn", "9780451524935")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.provider").value("OpenLibrary"))
        .andExpect(jsonPath("$.isbn").value("9780451524935"))
        .andExpect(jsonPath("$.title").value("1984"))
        .andExpect(jsonPath("$.authors[0]").value("George Orwell"));
  }

  @Test
  void externalSearch_shouldReturn404() throws Exception {
    when(externalSearchService.findBookByISBN("0000000000000"))
        .thenThrow(new ResourceNotFoundException("Not found"));

    mockMvc
        .perform(
            get("/external-search")
                .param("isbn", "0000000000000")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void externalSearch_shouldReturn502() throws Exception {
    when(externalSearchService.findBookByISBN(anyString()))
        .thenThrow(new BadGatewayException("Google Books Service Unavailable"));

    mockMvc
        .perform(
            get("/external-search")
                .param("isbn", "9780451524935")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway());
  }
}
