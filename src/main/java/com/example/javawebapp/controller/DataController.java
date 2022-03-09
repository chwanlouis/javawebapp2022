package com.example.javawebapp.controller;

import com.example.javawebapp.model.Bar;
import com.example.javawebapp.pojo.ReturnsPojo;
import com.example.javawebapp.pojo.ReturnsResponsePojo;
import com.example.javawebapp.pojo.SymbolPojo;
import com.example.javawebapp.service.AlphavantageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class DataController {

    @Autowired
    AlphavantageDataService alphavantageDataService;

    @RequestMapping(value={"/api/getReturns"})
    public ReturnsResponsePojo getReturns(@RequestParam String symbol, @RequestParam String from,
                                          @RequestParam String to) throws IOException, ParseException {
        return alphavantageDataService.requestAnnualizedReturns(symbol, from, to);
    }

    @RequestMapping(value={"/api/getSymbol"})
    public List<SymbolPojo> symbolSearch(@RequestParam String keyword) {
        List<SymbolPojo> symbolPojoList = alphavantageDataService.requestSymbolSearch(keyword);
        return symbolPojoList;
    }

    @RequestMapping(value={"/api/test"})
    public String testing() {
        return "testing";
    }

}
