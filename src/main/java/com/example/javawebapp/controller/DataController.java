package com.example.javawebapp.controller;

import com.example.javawebapp.model.Bar;
import com.example.javawebapp.pojo.ReturnsPojo;
import com.example.javawebapp.pojo.ReturnsResponsePojo;
import com.example.javawebapp.pojo.SymbolPojo;
import com.example.javawebapp.service.AlphavantageDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class DataController {

    Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    AlphavantageDataService alphavantageDataService;

    @RequestMapping(value={"/api/getReturns"})
    public ReturnsResponsePojo getReturns(@RequestParam String symbol, @RequestParam String from,
                                          @RequestParam String to) {
        logger.info(String.format("Requesting: /api/getReturns?symbol=%s&from=%s&to=%s", symbol, from, to));
        return alphavantageDataService.requestAnnualizedReturns(symbol, from, to);
    }

    @RequestMapping(value={"/api/getSymbol"})
    public List<SymbolPojo> symbolSearch(@RequestParam String keyword) {
        logger.info(String.format("Requesting: /api/getSymbol?keyword=%s", keyword));
        return alphavantageDataService.requestSymbolSearch(keyword);
    }

    @RequestMapping(value={"/api/test"})
    public String testing() {
        logger.info("Requesting: /api/test");
        return "testing";
    }

}
