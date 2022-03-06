package com.example.javawebapp.repository;

import com.example.javawebapp.model.Bar;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarRepository extends MongoRepository<Bar, String> {

}
