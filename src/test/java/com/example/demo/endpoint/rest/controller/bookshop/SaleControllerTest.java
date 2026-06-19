package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.service.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean SaleService saleService;

  @Test
  void getAll_returns200() throws Exception {
    when(saleService.findByDateBetween(any(), any())).thenReturn(List.of());
    mockMvc.perform(get("/sales")).andExpect(status().isOk());
  }

  @Test
  void getAll_withDates() throws Exception {
    when(saleService.findByDateBetween(any(), any())).thenReturn(List.of());
    mockMvc
        .perform(
            get("/sales")
                .param("from", "2024-01-01")
                .param("to", "2024-12-31"))
        .andExpect(status().isOk());
  }

  @Test
  void getById_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(saleService.findById(id)).thenReturn(new SaleResponse());
    mockMvc.perform(get("/sales/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void save_returns201() throws Exception {
    when(saleService.save(any())).thenReturn(new SaleResponse());
    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CreateSaleDTO(LocalDate.now(), List.of(UUID.randomUUID())))))
        .andExpect(status().isCreated());
  }
}
