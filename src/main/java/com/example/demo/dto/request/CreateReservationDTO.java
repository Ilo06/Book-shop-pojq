package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateReservationDTO {
    private LocalDate date;
    private List<CreateArrivalBookDTO> books;
}
