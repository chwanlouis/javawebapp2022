package com.example.javawebapp.repository;

import com.example.javawebapp.model.Symbol;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolRepository extends MongoRepository<Symbol, String> {
}
