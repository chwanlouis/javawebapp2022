package com.example.javawebapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReturnsResponsePojo {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("financialMetric")
    private ReturnsPojo returnsPojo;

}
