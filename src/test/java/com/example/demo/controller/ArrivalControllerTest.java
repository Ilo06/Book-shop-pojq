package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ArrivalBookLine;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.endpoint.rest.controller.bookshop.ArrivalController;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ArrivalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArrivalController.class, GlobalExceptionHandler.class})
class ArrivalControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private ArrivalService arrivalService;

  @Test
  void listArrivals_shouldReturn200() throws Exception {
    when(arrivalService.findAll()).thenReturn(List.of());

    mockMvc
        .perform(get("/arrivals").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getArrival_shouldReturn200() throws Exception {
    UUID id = UUID.randomUUID();
    ArrivalResponse response =
        ArrivalResponse.builder()
            .id(id)
            .arrivalDateTime(Instant.now())
            .books(List.of(new ArrivalBookLine(UUID.randomUUID(), 3)))
            .build();
    when(arrivalService.findById(id)).thenReturn(response);

    mockMvc
        .perform(get("/arrivals/{arrivalId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id.toString()));
  }

  @Test
  void getArrival_shouldReturn404() throws Exception {
    UUID id = UUID.randomUUID();
    when(arrivalService.findById(id))
        .thenThrow(new ResourceNotFoundException("Arrival not found with id: " + id));

    mockMvc
        .perform(get("/arrivals/{arrivalId}", id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void createArrival_shouldReturn201() throws Exception {
    QuantifiedBookCopyDTO qtyDTO = new QuantifiedBookCopyDTO(UUID.randomUUID(), 5);
    CreateArrivalDTO input = new CreateArrivalDTO(Instant.now(), List.of(qtyDTO));

    ArrivalResponse response =
        ArrivalResponse.builder()
            .id(UUID.randomUUID())
            .arrivalDateTime(Instant.now())
            .books(List.of())
            .build();
    when(arrivalService.create(any(CreateArrivalDTO.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/arrivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void createArrival_shouldReturn400_onMalformedBody() throws Exception {
    mockMvc
        .perform(post("/arrivals").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());
  }
}
