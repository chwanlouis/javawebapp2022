package com.example.javawebapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class TimeSeriesDailyResponsePojo {

    @JsonProperty("Meta Data")
    private TimeSeriesDailyMetadataPojo metaData;

    @JsonProperty("Time Series (Daily)")
    private Map<String, TimeSeriesDailyPojo> timeSeriesDaily;

}
