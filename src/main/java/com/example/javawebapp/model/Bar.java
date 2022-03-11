package com.example.javawebapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Document
@NoArgsConstructor
public class Bar {

    @Id
    private String id;
    private LocalDate date;
    private String symbol;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private BigDecimal adjClose;

    public Bar(LocalDate date, String symbol, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
               Long volume, BigDecimal adjClose) {
        this.date = date;
        this.symbol = symbol;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.adjClose = adjClose;
    }

}
