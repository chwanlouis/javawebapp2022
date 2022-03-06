package com.example.javawebapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;


@Data
@Document
public class Bar {

    @Id
    private String id;
    private LocalDate date;
    private String symbol;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Double adjClose;

    public Bar(LocalDate date, String symbol, Double open, Double high, Double low, Double close, Double volume,
               Double adjClose) {
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
