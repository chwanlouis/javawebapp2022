package com.example.javawebapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SymbolSearchResponsePojo {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("results")
    private List<SymbolPojo> symbolSearchPojo;

}
