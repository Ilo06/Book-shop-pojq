package com.example.demo.endpoint.rest.controller.health;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.repository.DummyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthDbController.class)
class HealthDbControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean DummyRepository dummyRepository;

  @Test
  void healthDb_ok() throws Exception {
    when(dummyRepository.findAll()).thenReturn(List.of(new com.example.demo.repository.model.Dummy()));
    mockMvc.perform(get("/health/db")).andExpect(status().isOk());
  }

  @Test
  void healthDb_ko() throws Exception {
    when(dummyRepository.findAll()).thenReturn(List.of());
    mockMvc.perform(get("/health/db")).andExpect(status().isInternalServerError());
  }
}
