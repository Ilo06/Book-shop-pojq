package com.example.demo.entity.keys;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ArrivalBookId implements Serializable {
    private UUID arrivalId;
    private UUID bookId;
}
