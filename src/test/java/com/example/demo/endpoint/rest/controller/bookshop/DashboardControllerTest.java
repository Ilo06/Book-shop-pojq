package com.example.demo.endpoint.rest.controller.bookshop;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.response.DashboardResponse;
import com.example.demo.service.DashboardService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

  @Autowired MockMvc mockMvc;
  @MockBean DashboardService dashboardService;

  @Test
  void getTodayRevenue() throws Exception {
    when(dashboardService.getTodayRevenue()).thenReturn(new DashboardResponse.TodayRevenue());
    mockMvc.perform(get("/dashboard/today-revenue")).andExpect(status().isOk());
  }

  @Test
  void getMonthlyRevenue() throws Exception {
    when(dashboardService.getMonthlyRevenue()).thenReturn(new DashboardResponse.MonthlyRevenue());
    mockMvc.perform(get("/dashboard/monthly-revenue")).andExpect(status().isOk());
  }

  @Test
  void getBooksInStock() throws Exception {
    when(dashboardService.getBooksInStock()).thenReturn(List.of());
    mockMvc.perform(get("/dashboard/books-in-stock")).andExpect(status().isOk());
  }

  @Test
  void getLowStock() throws Exception {
    when(dashboardService.getLowStock()).thenReturn(List.of());
    mockMvc.perform(get("/dashboard/low-stock")).andExpect(status().isOk());
  }

  @Test
  void getTopSellers() throws Exception {
    when(dashboardService.getTopSellers(10)).thenReturn(List.of());
    mockMvc.perform(get("/dashboard/top-sellers")).andExpect(status().isOk());
  }

  @Test
  void getTopSellers_withLimit() throws Exception {
    when(dashboardService.getTopSellers(5)).thenReturn(List.of());
    mockMvc.perform(get("/dashboard/top-sellers?limit=5")).andExpect(status().isOk());
  }

  @Test
  void getRevenueByGenre() throws Exception {
    when(dashboardService.getRevenueByGenre()).thenReturn(List.of());
    mockMvc.perform(get("/dashboard/revenue-by-genre")).andExpect(status().isOk());
  }
}
