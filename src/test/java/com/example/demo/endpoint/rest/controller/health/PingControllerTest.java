package com.example.demo.endpoint.rest.controller.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PingController.class)
class PingControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean com.example.demo.repository.DummyRepository dummyRepository;
  @MockBean com.example.demo.repository.DummyUuidRepository dummyUuidRepository;

  @Test
  void ping_returnsPong() throws Exception {
    mockMvc.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("pong"));
  }
}
