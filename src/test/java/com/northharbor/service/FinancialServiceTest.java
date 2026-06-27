package com.northharbor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.northharbor.model.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FinancialServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FinancialService financialService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRetrieveAll() throws IOException {

        //given

        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("currencies.json").getFile());
        CurrencyList currencyList = mapper.readValue(file, CurrencyList.class);

        ResponseEntity<CurrencyList> responseEntity = new ResponseEntity(currencyList, HttpStatus.OK);
        String latestURL = "null/latest?access_key=null";
        when(restTemplate.getForEntity(latestURL, CurrencyList.class)).thenReturn(responseEntity);

        //when

        CurrencyList result = financialService.retrieveLatest(null, null);

        //then
        Assertions.assertThat(result.getRates().size()).isEqualTo(170);
        Assertions.assertThat(result.isSuccess()).isEqualTo(true);
        Assertions.assertThat(result.getBase()).isEqualTo("EUR");
        Assertions.assertThat(result.getTimestamp()).isPositive();
    }


    @Test
    public void testRetrieveWithBaseAndList() throws IOException {
        //given

        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("currencies.json").getFile());
        CurrencyList currencyList = mapper.readValue(file, CurrencyList.class);

        ResponseEntity<CurrencyList> responseEntity = new ResponseEntity(currencyList, HttpStatus.OK);
        String latestURL = "null/latest?access_key=null&base=EUR&symbols=USD, GBP, CHF";
        when(restTemplate.getForEntity(latestURL, CurrencyList.class)).thenReturn(responseEntity);


        //when
        CurrencyList result = financialService.retrieveLatest("EUR", "USD, GBP, CHF");

        //then
        Assertions.assertThat(result.getRates().size()).isEqualTo(170);
        Assertions.assertThat(result.isSuccess()).isEqualTo(true);
        Assertions.assertThat(result.getBase()).isEqualTo("EUR");
        Assertions.assertThat(result.getTimestamp()).isPositive();

    }

    @Test
    public void testRetrieveTimeseries() throws IOException {
        //given
        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("timeseries.json").getFile());
        TimeseriesList timeseriesList = mapper.readValue(file, TimeseriesList.class);

        ResponseEntity<TimeseriesList> responseEntity = new ResponseEntity(timeseriesList, HttpStatus.OK);
        String latestURL = "null/timeseries?access_key=null&start_date=2023-01-01&end_date=2023-01-10";
        when(restTemplate.getForEntity(latestURL, TimeseriesList.class)).thenReturn(responseEntity);
        //when
        TimeseriesList result = financialService.getTimeSeries("2023-01-01", "2023-01-10");

        //then
        Assertions.assertThat(result.getRates().size()).isEqualTo(3);
        Assertions.assertThat(result.isSuccess()).isEqualTo(true);
        Assertions.assertThat(result.getBase()).isEqualTo("EUR");

    }

    @Test
    public void testRetrieveFluctuations() throws IOException {
        //given
        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("fluctuation.json").getFile());
        FluctuationList fluctuationList = mapper.readValue(file, FluctuationList.class);

        ResponseEntity<FluctuationList> responseEntity = new ResponseEntity(fluctuationList, HttpStatus.OK);
        String latestURL = "null/fluctuation?access_key=null&start_date=2023-01-01&end_date=2023-01-10";
        when(restTemplate.getForEntity(latestURL, FluctuationList.class)).thenReturn(responseEntity);
        //when
        FluctuationList result = financialService.getFluctuation("2023-01-01", "2023-01-10");

        //then
        Assertions.assertThat(result.getRates().size()).isEqualTo(2);
        Assertions.assertThat(result.isSuccess()).isEqualTo(true);
        Assertions.assertThat(result.getBase()).isEqualTo("EUR");

    }

    @Test
    public void testRetrieveSymbols() throws IOException {
        //given
        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("symbols.json").getFile());
        SymbolList fluctuationList = mapper.readValue(file, SymbolList.class);

        ResponseEntity<SymbolList> responseEntity = new ResponseEntity(fluctuationList, HttpStatus.OK);
        String latestURL = "null/symbols?access_key=null";
        when(restTemplate.getForEntity(latestURL, SymbolList.class)).thenReturn(responseEntity);
        //when
        List<SymbolItem> result = financialService.getSymbols();

        //then
        Assertions.assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void testConvertEndpoint() throws IOException {
        //given
        mapper.registerModule(new JavaTimeModule());

        File file = new File(this.getClass().getClassLoader().getResource("convert.json").getFile());
        ConvertItem convertItem = mapper.readValue(file, ConvertItem.class);

        ResponseEntity<ConvertItem> responseEntity = new ResponseEntity(convertItem, HttpStatus.OK);
        String latestURL = "null/convert?access_key=null&from=GBP&to=JPY&amount=25";
        when(restTemplate.getForEntity(latestURL, ConvertItem.class)).thenReturn(responseEntity);
        //when
        ConvertItem result = financialService.convert("GBP", "JPY", "25");

        //then
        Assertions.assertThat(result.getQuery().getAmount()).isEqualTo(25);
        Assertions.assertThat(result.isSuccess()).isEqualTo(true);

    }
}
