package com.example.javawebapp.service;

import com.example.javawebapp.exception.AlphavantageDataExecption;
import com.example.javawebapp.model.Bar;
import com.example.javawebapp.pojo.*;
import com.example.javawebapp.repository.BarRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.lang.Math;


@Service
public class AlphavantageDataService {

    @Autowired
    BarRepository barRepository;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static RestTemplate restTemplate = new RestTemplate();
    private static String mainURL = "https://www.alphavantage.co/query";

    public static LocalDate stringToLocalDate(String dateString, String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.parse(dateString, formatter);
    }

    public static String generateUrl(String mainURL, Map<String, String> params) {

        StringBuilder mapAsString = new StringBuilder(mainURL);
        mapAsString.append("?");
        for (String key : params.keySet()) {
            mapAsString.append(key + "=" + params.get(key) + "&");
        }
        mapAsString.delete(mapAsString.length()-1, mapAsString.length());
        return mapAsString.toString();
    }

    public Map<String, String> generateParamMap(String function) throws AlphavantageDataExecption {
        Map<String, String> params = new HashMap<>();
        params.put("apikey", apiKey);
        if (function.equalsIgnoreCase("SYMBOL_SEARCH")) {
            params.put("function", "SYMBOL_SEARCH");
        } else if (function.equalsIgnoreCase("TIME_SERIES_DAILY")) {
            params.put("function", "TIME_SERIES_DAILY");
        } else {
            throw new AlphavantageDataExecption(String.format("Unknown function input: %s", function));
        }
        return params;
    }

    public String getSymbolSearch(String keyword) throws AlphavantageDataExecption {
        Map<String, String> params = generateParamMap("SYMBOL_SEARCH");
        params.put("keywords", keyword);
        String url = generateUrl(mainURL, params);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    public String getDailyTimeSeries(String symbol, String outputSize) throws AlphavantageDataExecption {
        Map<String, String> params = generateParamMap("TIME_SERIES_DAILY");
        params.put("symbol", symbol);
        if (outputSize.equalsIgnoreCase("full")) {
            // download full data set if first time calling
            params.put("outputsize", "full");
        } else if (outputSize.equalsIgnoreCase("compact")) {
            params.put("outputsize", "compact");
        } else {
            throw new AlphavantageDataExecption(String.format("Invalid input for param outputsize: %s", outputSize));
        }
        String url = generateUrl(mainURL, params);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    public static List<Bar> generateBarDataFromPojo (Map<String, TimeSeriesDailyPojo> timeSeriesDailyPojoMap,
                                                     String symbol) {
        List<Bar> barDataList = new LinkedList<>();
        for (Map.Entry<String, TimeSeriesDailyPojo> set : timeSeriesDailyPojoMap.entrySet()) {
            Bar bar = new Bar(
                    LocalDate.parse(set.getKey()),
                    symbol,
                    new BigDecimal(set.getValue().getOpen()),
                    new BigDecimal(set.getValue().getHigh()),
                    new BigDecimal(set.getValue().getLow()),
                    new BigDecimal(set.getValue().getClose()),
                    Long.parseLong(set.getValue().getVolume()),
                    new BigDecimal(set.getValue().getClose())
            );
            barDataList.add(bar);
        }
        return barDataList;
    }

    public SymbolSearchResponsePojo requestSymbolSearch(String keyword){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (keyword.matches("^.*[^a-zA-Z].*$")) {
                throw new AlphavantageDataExecption("Keyword contains non alphabetic characters");
            }
            String symbolSearchResponse = getSymbolSearch(keyword);
            SymbolSearchPojo symbolSearchPojo = objectMapper.readValue(symbolSearchResponse, SymbolSearchPojo.class);
            SymbolSearchResponsePojo symbolSearchResponsePojo = new SymbolSearchResponsePojo();
            symbolSearchResponsePojo.setStatus("Succeed");
            symbolSearchResponsePojo.setSymbolSearchPojo(symbolSearchPojo.getBestMatches());
            return symbolSearchResponsePojo;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            SymbolSearchResponsePojo symbolSearchResponsePojo = new SymbolSearchResponsePojo();
            symbolSearchResponsePojo.setStatus("Failed");
            symbolSearchResponsePojo.setMessage("Internal server error");
            return symbolSearchResponsePojo;
        } catch (AlphavantageDataExecption e) {
            SymbolSearchResponsePojo symbolSearchResponsePojo = new SymbolSearchResponsePojo();
            symbolSearchResponsePojo.setStatus("Failed");
            symbolSearchResponsePojo.setMessage(e.getMessage());
            return symbolSearchResponsePojo;
        }

    }

    public void requestData(String symbol) throws AlphavantageDataExecption{
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dailyTimeSeriesResponse = getDailyTimeSeries(symbol, "full");
            // try catch error message
            try {
                ErrorMessagePojo errorMessagePojo = objectMapper.readValue(dailyTimeSeriesResponse,
                        ErrorMessagePojo.class);
                throw new AlphavantageDataExecption(String.format(
                        "Unknown symbol input: %s, please use /symbolSearch to find correct symbol", symbol));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            TimeSeriesDailyResponsePojo timeSeriesDailyResponsePojo = objectMapper.readValue(dailyTimeSeriesResponse,
                    TimeSeriesDailyResponsePojo.class);
            Map<String, TimeSeriesDailyPojo> timeSeriesDailyPojoMap = timeSeriesDailyResponsePojo.getTimeSeriesDaily();
            List<Bar> barDataList = generateBarDataFromPojo(timeSeriesDailyPojoMap, symbol);
            barRepository.saveAll(barDataList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void requestData(String symbol, LocalDate latestDate) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dailyTimeSeriesResponse = getDailyTimeSeries(symbol, "compact");
            TimeSeriesDailyResponsePojo timeSeriesDailyResponsePojo = objectMapper.readValue(dailyTimeSeriesResponse,
                    TimeSeriesDailyResponsePojo.class);
            Map<String, TimeSeriesDailyPojo> timeSeriesDailyPojoMap = timeSeriesDailyResponsePojo.getTimeSeriesDaily();
            List<Bar> barDataList = new LinkedList<>();
            for (Bar bar: generateBarDataFromPojo(timeSeriesDailyPojoMap, symbol)) {
                if (latestDate.until(bar.getDate(), ChronoUnit.DAYS) > 0) {
                    barDataList.add(bar);
                }
            }
            barRepository.saveAll(barDataList);
        } catch (AlphavantageDataExecption | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Double calculateAnnualizedReturn(Bar barFrom, Bar barTo) throws AlphavantageDataExecption{
        if (null == barFrom.getClose() || null == barTo.getClose() ) {
            throw new AlphavantageDataExecption("Close price is null or NaNs instead of numeric values");
        }
        Double closeFrom = barFrom.getClose().doubleValue();
        Double closeTo = barTo.getClose().doubleValue();
        LocalDate dateFrom = barFrom.getDate();
        LocalDate dateTo = barTo.getDate();
        if (closeFrom.isNaN() || closeTo.isNaN()) {
            throw new AlphavantageDataExecption("Close price is null or NaNs instead of numeric values");
        } else if (null == dateFrom || null == dateTo) {
            throw new AlphavantageDataExecption("Date provide is null instead of LocalDate");
        }
        Double returns = (closeTo - closeFrom) / closeFrom;
        Double period = 365.0 / dateFrom.until(dateTo, ChronoUnit.DAYS);
        return Math.pow(1 + returns, period) - 1;
    }

    public ReturnsResponsePojo requestAnnualizedReturns(String symbol, String from, String to) {
        ReturnsResponsePojo returnsResponsePojo = new ReturnsResponsePojo();
        Double annualizedReturn;
        try {
            LocalDate dateFrom = stringToLocalDate(from, "yyyyMMdd");
            LocalDate dateTo = stringToLocalDate(to, "yyyyMMdd");
            // search symbol if exist in database
            Bar barLatest = barRepository.findTopBySymbolOrderByDateDesc(symbol);
            if (null == barLatest) {
                // update database if symbol not found
                requestData(symbol);
            } else if (barLatest.getDate().until(LocalDate.now(), ChronoUnit.DAYS) > 1) {
                // update database if data outdated
                requestData(symbol, barLatest.getDate());
            }
            // Get closest date that greater than or equals to give fromDate
            Bar barFrom = barRepository.findTopBySymbolAndDateGreaterThanEqualOrderByDate(symbol, dateFrom);
            // Get closest date that less than or equals to give toDate
            // plusDays calibrate hong kong time zone
            Bar barTo = barRepository.findTopBySymbolAndDateLessThanOrderByDateDesc(symbol, dateTo.plusDays(1));
            if (null != barFrom && null != barTo) {
                annualizedReturn = calculateAnnualizedReturn(barFrom, barTo);
                ReturnsPojo returnsPojo = new ReturnsPojo();
                returnsPojo.setAnnualizedReturn(annualizedReturn);
                returnsPojo.setDateFrom(barFrom.getDate());
                returnsPojo.setDateTo(barTo.getDate());
                returnsPojo.setClosePriceFrom(barFrom.getClose());
                returnsPojo.setClosePriceTo(barTo.getClose());
                returnsPojo.setSymbol(symbol);
                returnsResponsePojo.setReturnsPojo(returnsPojo);
                returnsResponsePojo.setStatus("Succeed");
                return returnsResponsePojo;
            }
            throw new AlphavantageDataExecption("Database error, please inform system admin");
        } catch (DateTimeException e) {
            e.printStackTrace();
            returnsResponsePojo.setMessage("Invalid date format, please provide in format of yyyyMMdd");
            returnsResponsePojo.setStatus("Failed");
            return returnsResponsePojo;
        } catch (AlphavantageDataExecption e) {
            e.printStackTrace();
            returnsResponsePojo.setMessage(e.getMessage());
            returnsResponsePojo.setStatus("Failed");
            return returnsResponsePojo;
        }
    }

}
