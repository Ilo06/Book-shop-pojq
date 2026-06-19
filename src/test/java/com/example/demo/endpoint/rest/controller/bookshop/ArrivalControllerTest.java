package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.service.ArrivalService;
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

@WebMvcTest(ArrivalController.class)
class ArrivalControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean ArrivalService arrivalService;

  @Test
  void listArrivals_returns200() throws Exception {
    when(arrivalService.findAll()).thenReturn(List.of());
    mockMvc.perform(get("/arrivals")).andExpect(status().isOk());
  }

  @Test
  void getArrival_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(arrivalService.findById(id)).thenReturn(new ArrivalResponse());
    mockMvc.perform(get("/arrivals/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void createArrival_returns201() throws Exception {
    when(arrivalService.create(any())).thenReturn(new ArrivalResponse());
    mockMvc
        .perform(
            post("/arrivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CreateArrivalDTO(
                            LocalDate.now(),
                            List.of(new QuantifiedBookCopyDTO(UUID.randomUUID(), 5))))))
        .andExpect(status().isCreated());
  }
}
