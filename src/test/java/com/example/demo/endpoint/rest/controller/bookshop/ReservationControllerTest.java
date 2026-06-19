package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.request.PatchReservationDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean ReservationService reservationService;

  @Test
  void listReservations_returns200() throws Exception {
    when(reservationService.findAll(null)).thenReturn(List.of());
    mockMvc.perform(get("/reservations")).andExpect(status().isOk());
  }

  @Test
  void listReservations_withStatus() throws Exception {
    when(reservationService.findAll(ReservationStatus.PENDING)).thenReturn(List.of());
    mockMvc
        .perform(get("/reservations").param("status", "PENDING"))
        .andExpect(status().isOk());
  }

  @Test
  void getReservation_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(reservationService.findById(id)).thenReturn(new ReservationResponse());
    mockMvc.perform(get("/reservations/{id}", id)).andExpect(status().isOk());
  }

  @Test
  void createReservation_returns201() throws Exception {
    when(reservationService.create(any())).thenReturn(new ReservationResponse());
    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CreateReservationDTO(
                            LocalDate.now(),
                            List.of(new QuantifiedBookCopyDTO(UUID.randomUUID(), 2))))))
        .andExpect(status().isCreated());
  }

  @Test
  void patchReservation_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(reservationService.patch(any(), any())).thenReturn(new ReservationResponse());
    mockMvc
        .perform(
            patch("/reservations/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new PatchReservationDTO(ReservationStatus.CONFIRMED))))
        .andExpect(status().isOk());
  }

  @Test
  void deleteReservation_returns204() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(reservationService).delete(id);
    mockMvc.perform(delete("/reservations/{id}", id)).andExpect(status().isNoContent());
  }
}
