package com.example.javawebapp.repository;

import com.example.javawebapp.model.Bar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BarRepository extends MongoRepository<Bar, String> {

    public Bar findTopBySymbolOrderByDateDesc(String symbol);

    public Bar findTopBySymbolAndDateGreaterThanEqualOrderByDate(String symbol, LocalDate date);

    public Bar findTopBySymbolAndDateLessThanEqualOrderByDateDesc(String symbol, LocalDate date);

}
