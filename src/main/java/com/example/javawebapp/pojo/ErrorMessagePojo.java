package com.example.javawebapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class ErrorMessagePojo {

    @JsonProperty("Error Message")
    private String errorMessage;

}
