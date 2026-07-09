package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RevenueResponse {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TodayRevenue {
    private BigDecimal todayRevenue;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MonthlyRevenue {
    private BigDecimal monthlyRevenue;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TopSellerEntry {
    private UUID bookId;
    private String title;
    private Long unitsSold;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RevenueByGenreEntry {
    private UUID genreId;
    private String genreName;
    private BigDecimal revenue;
  }
}
