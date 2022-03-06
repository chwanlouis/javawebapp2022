package com.example.javawebapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Document
public class Symbol {

    @Id
    private String id;
    private String symbol;

    public Symbol(String symbol) {
        this.symbol = symbol;
    }

}
