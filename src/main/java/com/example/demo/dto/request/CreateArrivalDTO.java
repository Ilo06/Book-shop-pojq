package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateArrivalDTO {
    private LocalDate arrivalDate;
    private List<CreateArrivalBookDTO> books;
}
