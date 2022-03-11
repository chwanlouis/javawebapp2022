package com.example.javawebapp;

import com.example.javawebapp.exception.AlphavantageDataExecption;
import com.example.javawebapp.model.Bar;
import com.example.javawebapp.pojo.ReturnsResponsePojo;
import com.example.javawebapp.pojo.SymbolSearchResponsePojo;
import com.example.javawebapp.service.AlphavantageDataService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JavawebappApplicationTests {

	@Autowired
	AlphavantageDataService alphavantageDataService;

	public static LocalDate stringToLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDate.parse(dateString, formatter);
	}

	public static LocalDate getPreviousBusinessDate() {
		LocalDate previousDay = LocalDate.now().minusDays(1);
		while (previousDay.getDayOfWeek() == DayOfWeek.SATURDAY || previousDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
			previousDay = previousDay.minusDays(1);
		}
		return previousDay;
	}

	public static Double roundOff(Double x, int position) {
		Double a = x;
		double temp = Math.pow(10.0, position);
		a *= temp;
		return Math.round(a) / temp;
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

	@DisplayName("Test annualized return calculation correct or not")
	public void testAnnualizedReturnCalculation(Bar barFrom, Bar barTo, Double result) throws AlphavantageDataExecption{
		Double calculatedResult = alphavantageDataService.calculateAnnualizedReturn(barFrom, barTo);
		assertEquals(roundOff(calculatedResult, 6), roundOff(result, 6));
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

	@DisplayName("Test annualized return with correct symbol and both date are hoilday")
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

	@DisplayName("Test annualized return with correct symbol and both date are out of time range")
	@Test
	public void testAnnualizedReturnBothDateOutOfRange() {
		String symbol = "TSM";
		String dateFrom = "19600101";
		String dateTo = "20701231";
		String businessDateFrom = "19991101";
		LocalDate businessDateTo = getPreviousBusinessDate();
		ReturnsResponsePojo returnsResponsePojo = alphavantageDataService.requestAnnualizedReturns(symbol, dateFrom, dateTo);
		assertEquals(returnsResponsePojo.getStatus(), "Succeed");
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateFrom(), stringToLocalDate(businessDateFrom));
		assertEquals(returnsResponsePojo.getReturnsPojo().getDateTo(), businessDateTo);
	}

	@Test
	void contextLoads() {
		try {
			// test set 1
			Bar testBarFrom1 = new Bar();
			testBarFrom1.setDate(LocalDate.parse("2021-01-01"));
			testBarFrom1.setClose(BigDecimal.valueOf(10));
			Bar testBarTo1 = new Bar();
			testBarTo1.setDate(LocalDate.parse("2022-01-01"));
			testBarTo1.setClose(BigDecimal.valueOf(11));
			Double result1 = 0.1;
			testAnnualizedReturnCalculation(testBarFrom1, testBarTo1, result1);
			// test set 2
			Bar testBarFrom2 = new Bar();
			testBarFrom2.setDate(LocalDate.parse("2021-01-01"));
			testBarFrom2.setClose(BigDecimal.valueOf(10.4));
			Bar testBarTo2 = new Bar();
			testBarTo2.setDate(LocalDate.parse("2021-07-01"));
			testBarTo2.setClose(BigDecimal.valueOf(11.9));
			Double result2 = 0.312191;
			testAnnualizedReturnCalculation(testBarFrom2, testBarTo2, result2);
			// test set 3      close price is null
			Bar testBarFrom3 = new Bar();
			testBarFrom3.setDate(LocalDate.parse("2021-03-10"));
			Bar testBarTo3 = new Bar();
			testBarTo3.setDate(LocalDate.parse("2021-03-11"));
			Double result3 = null;
			Exception exception3 = assertThrows(AlphavantageDataExecption.class, () -> {
				testAnnualizedReturnCalculation(testBarFrom3, testBarTo3, result3);
			});
			String expectedMessage3 = "Close price is null or NaNs instead of numeric values";
			String actualMessage3 = exception3.getMessage();
			assertTrue(actualMessage3.contains(expectedMessage3));
			// test set 4      date is null
			Bar testBarFrom4 = new Bar();
			testBarFrom4.setClose(BigDecimal.valueOf(14.4));
			Bar testBarTo4 = new Bar();
			testBarTo4.setClose(BigDecimal.valueOf(17.8));
			Double result4 = null;
			Exception exception4 = assertThrows(AlphavantageDataExecption.class, () -> {
				testAnnualizedReturnCalculation(testBarFrom4, testBarTo4, result4);
			});
			String expectedMessage4 = "Date provide is null instead of LocalDate";
			String actualMessage4 = exception4.getMessage();
			assertTrue(actualMessage4.contains(expectedMessage4));
		} catch (AlphavantageDataExecption e) {
			e.printStackTrace();
		}
	}

}