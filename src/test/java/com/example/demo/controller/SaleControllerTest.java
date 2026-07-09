package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateSaleDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.RevenueResponse;
import com.example.demo.dto.response.SaleBookCopyResponse;
import com.example.demo.dto.response.SaleResponse;
import com.example.demo.endpoint.rest.controller.bookshop.SaleController;
import com.example.demo.entity.enums.SaleStatus;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({SaleController.class, GlobalExceptionHandler.class})
class SaleControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private SaleService saleService;

  @Test
  void getAll_shouldReturn200() throws Exception {
    when(saleService.findByDateBetween(any(), any())).thenReturn(List.of());

    mockMvc
        .perform(get("/sales").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getById_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    SaleResponse response =
        SaleResponse.builder()
            .id(id)
            .creationDateTime(Instant.now())
            .saleStatus(SaleStatus.PENDING)
            .isReservation(false)
            .books(List.of())
            .build();
    when(saleService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/sales/{saleId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id.toString()));
  }

  @Test
  void getById_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(saleService.findById(id))
        .thenThrow(new ResourceNotFoundException("Sale with id " + id + " not found"));

    mockMvc
        .perform(get("/sales/{saleId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void save_shouldReturn201() throws Exception {
    CreateSaleDTO input = new CreateSaleDTO();
    input.setCreationDateTime(Instant.now());
    input.setQuantifiedBookCopyList(List.of(new QuantifiedBookCopyDTO(UUID.randomUUID(), 1)));
    input.setIsReservation(false);

    SaleResponse response =
        SaleResponse.builder()
            .id(UUID.randomUUID())
            .creationDateTime(Instant.now())
            .saleStatus(SaleStatus.PENDING)
            .isReservation(false)
            .books(
                List.of(
                    SaleBookCopyResponse.builder()
                        .bookCopyId(UUID.randomUUID())
                        .price(BigDecimal.valueOf(19.99))
                        .quantity(1)
                        .build()))
            .build();
    when(saleService.save(any(CreateSaleDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void save_shouldReturn400_onValidationError() throws Exception {
    CreateSaleDTO input = new CreateSaleDTO();

    mockMvc
        .perform(
            post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void finalize_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    SaleResponse response =
        SaleResponse.builder()
            .id(id)
            .creationDateTime(Instant.now())
            .saleStatus(SaleStatus.CONFIRMED)
            .isReservation(false)
            .books(List.of())
            .build();
    when(saleService.finalize(id, true)).thenReturn(response);

    mockMvc
        .perform(
            post("/sales/{saleId}/finalize", id)
                .param("confirm", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.saleStatus").value("CONFIRMED"));
  }

  @Test
  void finalize_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(saleService.finalize(id, true)).thenThrow(new ResourceNotFoundException("Sale not found"));

    mockMvc
        .perform(
            post("/sales/{saleId}/finalize", id)
                .param("confirm", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void todayRevenue_shouldReturn200() throws Exception {
    LocalDate now = LocalDate.now();
    when(saleService.getTodayRevenue(now))
        .thenReturn(new RevenueResponse.TodayRevenue(BigDecimal.valueOf(250.00)));

    mockMvc
        .perform(get("/sales/day-revenue?t=%s".formatted(now)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.todayRevenue").value(250.00));
  }

  @Test
  void monthlyRevenue_shouldReturn200() throws Exception {
    LocalDate now = LocalDate.now();
    when(saleService.getMonthlyRevenue(now))
        .thenReturn(new RevenueResponse.MonthlyRevenue(BigDecimal.valueOf(5000.00)));

    mockMvc
        .perform(get("/sales/month-revenue?t=%s".formatted(now)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.monthlyRevenue").value(5000.00));
  }

  @Test
  void topSellers_shouldReturn200() throws Exception {
    when(saleService.getTopSellers(5))
        .thenReturn(List.of(new RevenueResponse.TopSellerEntry(UUID.randomUUID(), "Book A", 50L)));

    mockMvc
        .perform(get("/sales/top-sellers").param("limit", "5").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Book A"))
        .andExpect(jsonPath("$[0].unitsSold").value(50));
  }

  @Test
  void revenueByGenre_shouldReturn200() throws Exception {
    when(saleService.getRevenueByGenre())
        .thenReturn(
            List.of(
                new RevenueResponse.RevenueByGenreEntry(
                    UUID.randomUUID(), "Fiction", BigDecimal.valueOf(1200))));

    mockMvc
        .perform(get("/sales/revenue-by-genre").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].genreName").value("Fiction"))
        .andExpect(jsonPath("$[0].revenue").value(1200));
  }
}
