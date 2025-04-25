package com.surgeRetail.surgeRetail.document.store;

import lombok.Data;

import java.time.LocalTime;

@Data
public class OperatingHours {
    private String day;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
