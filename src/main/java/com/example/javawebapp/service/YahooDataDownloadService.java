package com.example.javawebapp.service;

import com.example.javawebapp.model.Bar;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class YahooDataDownloadService {

    public List<Bar> getHistoricalData(String symbol) throws IOException {

        Stock stock = YahooFinance.get(symbol);
        List<HistoricalQuote> historicalQuotes = stock.getHistory();
        List<Bar> barData = new LinkedList<>();

//        for (HistoricalQuote histQuote : historicalQuotes) {
//            Bar bar = new Bar();
//
//        }

        return barData;
    }

    public static void main () {

    }

}
