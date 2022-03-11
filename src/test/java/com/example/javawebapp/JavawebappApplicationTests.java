package com.example.javawebapp;

import com.example.javawebapp.pojo.ReturnsResponsePojo;
import com.example.javawebapp.pojo.SymbolSearchResponsePojo;
import com.example.javawebapp.service.AlphavantageDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JavawebappApplicationTests {

	@Autowired
	AlphavantageDataService alphavantageDataService;

	public static LocalDate stringToLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDate.parse(dateString, formatter);
	}

	@DisplayName("Test using keyword 'apple' in symbolSearch function returning non null results")
	@Test
	public void testSymbolSearch() {
		String keyword = "apple";
		SymbolSearchResponsePojo symbolSearchResponsePojo = alphavantageDataService.requestSymbolSearch(keyword);
		assertEquals(symbolSearchResponsePojo.getStatus(), "Succeed");
	}

	@DisplayName("Test using keyword '^hfyg*' in symbolSearch function returning error")
	@Test
	public void testSymbolSearchNonAlphaString() {
		String keyword = "^hfyg*";
		SymbolSearchResponsePojo symbolSearchResponsePojo = alphavantageDataService.requestSymbolSearch(keyword);
		assertEquals(symbolSearchResponsePojo.getStatus(), "Failed");
	}

	@DisplayName("Test annualized return with correct symbol and exact bar date")
	@Test
	public void testAnnualizedReturn() {
		String symbol = "AAPL";
		String dateFrom = "20220301";
		String dateTo = "20220308";
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Succeed");
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateFrom(), stringToLocalDate(dateFrom));
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateTo(), stringToLocalDate(dateTo));
	}

	@DisplayName("Test annualized return with wrong symbol")
	@Test
	public void testAnnualizedReturnWrongSymbol() {
		String symbol = "APPLE";
		String dateFrom = "20220301";
		String dateTo = "20220308";
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Failed");
	}

	@DisplayName("Test annualized return with correct symbol and from date is hoilday")
	@Test
	public void testAnnualizedReturnFromDateDrift() {
		String symbol = "IBM";
		String dateFrom = "20220227";
		String dateTo = "20220308";
		String businessDateFrom = "20220228";
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Succeed");
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateFrom(), stringToLocalDate(businessDateFrom));
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateTo(), stringToLocalDate(dateTo));
	}

	@DisplayName("Test annualized return with correct symbol and to date is hoilday")
	@Test
	public void testAnnualizedReturnToDateDrift() {
		String symbol = "QQQ";
		String dateFrom = "20220301";
		String dateTo = "20220305";
		String businessDateTo = "20220304";
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Succeed");
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateFrom(), stringToLocalDate(dateFrom));
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateTo(), stringToLocalDate(businessDateTo));
	}

	@DisplayName("Test annualized return with correct symbol and both date is hoilday")
	@Test
	public void testAnnualizedReturnBothDateDrift() {
		String symbol = "TSM";
		String dateFrom = "20220227";
		String dateTo = "20220305";
		String businessDateFrom = "20220228";
		String businessDateTo = "20220304";
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Succeed");
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateFrom(), stringToLocalDate(businessDateFrom));
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateTo(), stringToLocalDate(businessDateTo));
	}

	@Test
	void contextLoads() {
	}

}