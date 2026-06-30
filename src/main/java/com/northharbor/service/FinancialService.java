package com.northharbor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.northharbor.generator.ReportGenerator;
import com.northharbor.model.CurrencyList;
import com.northharbor.repository.SymbolRepository;
import com.northharbor.configuration.NorthHarborConfigurationProperties;
import com.northharbor.model.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FinancialService {

	@Autowired
	private NorthHarborConfigurationProperties properties;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ReportGenerator reportGenerator;

	@Autowired
	private SymbolRepository symbolRepository;

	private String apiHost;
	private String apiKey;

	private static String TIME_ZONE = "UTC";

	private static final Set<String> ALLOWED_CURRENCIES = Set.of("EUR", "USD", "GBP", "CHF");

	@PostConstruct
	public void init() {
		apiHost = properties.getEndpoint();
		apiKey = properties.getKey();
	}

	public <T> T retrieve(String basePath, String base, String symbols, Class<T> clazz) {

		StringBuilder latestURL = new StringBuilder(basePath);

		if (base != null) {
			latestURL.append("&base=");
			latestURL.append(base);
		}

		if (symbols != null) {
			latestURL.append("&symbols=");
			latestURL.append(symbols);
		}

		ResponseEntity<T> response = restTemplate.getForEntity(latestURL.toString(), clazz);
		HttpStatusCode statusCode = response.getStatusCode();
		HttpHeaders httpHeaders = response.getHeaders();

		logHttpHeadersAndStatus(httpHeaders, statusCode);
		return response.getBody();
	}

	public CurrencyList retrieveHistorical(String base, String symbols, String date) {
		return retrieve(apiHost + "/" + date + "?access_key=" + apiKey, base, symbols, CurrencyList.class);
	}

	public CurrencyList retrieveLatest(String base, String symbols) {
		return retrieve(apiHost + "/latest?access_key=" + apiKey, base, symbols, CurrencyList.class);
	}

	public List<CurrencyItem> retrieveLatestAsCurrencyItem(String selectedCurrency) {
		// if null by default EUR is base currency
		CurrencyList currencyList = retrieveLatest(selectedCurrency, null);

		return currencyList
				.getRates().entrySet().stream().map(item -> CurrencyItem.builder().fromCurrency(currencyList.getBase())
						.toCurrency(item.getKey()).rate(item.getValue()).date(currencyList.getDate()).build())
				.collect(Collectors.toList());

	}

	public BigDecimal calculateRate(String selectedCurrency, BigDecimal fromCurrency, BigDecimal toCurrency) {

		if (fromCurrency == null || toCurrency == null) {
			throw new IllegalArgumentException("Currency rates cannot be null.");
		}

		if (selectedCurrency.equals("EUR")) {
			return toCurrency;
		}

		if (fromCurrency.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalArgumentException("Base currency rate ('fromCurrency') cannot be zero.");
		}

		return toCurrency.divide(fromCurrency, 6, RoundingMode.HALF_UP);
	}

	public List<CurrencyItem> retrieveHistoricalAsCurrencyItem() {
		CurrencyList currencyList = retrieveHistorical(null, null, LocalDate.now(ZoneId.of(TIME_ZONE )).minusDays(1).toString());
		return currencyList
				.getRates().entrySet().stream().map(item -> CurrencyItem.builder().fromCurrency(currencyList.getBase())
						.toCurrency(item.getKey()).rate(item.getValue()).date(currencyList.getDate()).build())
				.collect(Collectors.toList());
	}

	public BigDecimal computeChange(String base, String symbol, String startDate, String endDate)
			throws JsonProcessingException {
		CurrencyList start = retrieveHistorical(base, symbol, startDate);
		CurrencyList end = retrieveHistorical(base, symbol, endDate);
		BigDecimal diff;

		Map<String, BigDecimal> startMap = start.getRates();
		Map<String, BigDecimal> endMap = end.getRates();
		BigDecimal startCurrency = startMap.get(symbol);
		BigDecimal endCurrency = endMap.get(symbol);
		diff = endCurrency.subtract(startCurrency);

		return diff;
	}

	public BigDecimal computeConversion(String base, String symbol, String amount) {
		CurrencyList result = retrieveLatest(base, symbol);
		Map<String, BigDecimal> rates = result.getRates();
		BigDecimal rate = rates.get(symbol);
		return rate.multiply(new BigDecimal(amount));
	}

	public ConvertItem convert(String from, String to, String amount) {
		StringBuilder builder = new StringBuilder(apiHost + "/convert?access_key=" + apiKey);
		builder.append("&from=");
		builder.append(from);
		builder.append("&to=");
		builder.append(to);
		builder.append("&amount=");
		builder.append(amount);

		return retrieve(builder.toString(), null, null, ConvertItem.class);
	}

	public TimeseriesList getTimeSeries(String startDate, String endDate) {
		StringBuilder builder = new StringBuilder(apiHost + "/timeseries?access_key=" + apiKey);
		builder.append("&start_date=");
		builder.append(startDate);
		builder.append("&end_date=");
		builder.append(endDate);

		return retrieve(builder.toString(), null, null, TimeseriesList.class);
	}

	public FluctuationList getFluctuation(String startDate, String endDate) {
		StringBuilder builder = new StringBuilder(apiHost + "/fluctuation?access_key=" + apiKey);
		builder.append("&start_date=");
		builder.append(startDate);
		builder.append("&end_date=");
		builder.append(endDate);

		return retrieve(builder.toString(), null, null, FluctuationList.class);
	}

	public List<SymbolItem> getSymbols() {
		StringBuilder builder = new StringBuilder(apiHost + "/symbols?access_key=" + apiKey);

		SymbolList symbolList = retrieve(builder.toString(), null, null, SymbolList.class);

		return symbolList.getSymbols().entrySet().stream()
				.map(s -> SymbolItem.builder().currencyCode(s.getKey()).currencyName(s.getValue()).build())
				.collect(Collectors.toList());
	}

	public String generateHistoricalReport(String fileName, String base, String date) throws IOException {
		CurrencyList historical = retrieveHistorical(base, null, date);
		return reportGenerator.generateHistoricalReport(historical, fileName, base, date);
	}

	public Path generateLatestReport(String fileName, String base) throws IOException {
		CurrencyList latest = retrieveLatest(null, null);
		return reportGenerator.generateLatestReport(latest, fileName, base);
	}

	public static String validateCurrency(String base) {

		if (base == null) {
			throw new IllegalArgumentException("Base currency is required");
		}

		String currency = base.trim().toUpperCase(Locale.ROOT);

		if (!ALLOWED_CURRENCIES.contains(currency)) {
			throw new IllegalArgumentException("Unsupported base currency code");
		}

		return currency;
	}

	private void logHttpHeadersAndStatus(HttpHeaders httpHeaders, HttpStatusCode statusCode) {

		int statsCodeValue = statusCode.value();

		String responseHeaders = httpHeaders.getAccessControlRequestHeaders().stream().collect(Collectors.joining());

		StringBuilder builder = new StringBuilder();
		builder.append("Response HTTP Status Code: ").append(statsCodeValue);
		builder.append("\n");
		builder.append("Response HTTP Headers list: ").append(responseHeaders);
		log.info(builder.toString());
	}
}
