package com.example.javawebapp.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ReturnsPojo {

    private String symbol;
    private Double annualizedReturn;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private BigDecimal closePriceFrom;
    private BigDecimal closePriceTo;

}
