package com.northharbor.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.northharbor.model.ConvertItem;
import com.northharbor.model.CurrencyList;
import com.northharbor.model.FluctuationList;
import com.northharbor.model.SymbolItem;
import com.northharbor.model.TimeseriesList;
import com.northharbor.service.FinancialService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BankApiController {

  @Autowired
  private FinancialService service;



  @GetMapping("/retrieveLatest")
  @ResponseBody
  public ResponseEntity<CurrencyList> retrieveLatest() {
    return ResponseEntity.ok().body(service.retrieveLatest(null, null));
  }

  @GetMapping("/retrieveHistorical")
  @ResponseBody
  public ResponseEntity<CurrencyList> retrieveHistorical(@RequestParam String date) {
    return ResponseEntity.ok().body(service.retrieveHistorical(null, null, date));
  }

  @GetMapping("/retrieveLatestWithBase")
  @ResponseBody
  public ResponseEntity<CurrencyList> retrieveLatestWithBase(@RequestParam String base) {
    return ResponseEntity.ok().body(service.retrieveLatest(base, null));
  }

  @GetMapping("/retrieveLatestWithBaseAndSymbols")
  @ResponseBody
  public ResponseEntity<CurrencyList> retrieveLatestWithBaseAndSymbols(@RequestParam String base,
      @RequestParam String symbols) {
    return ResponseEntity.ok().body(service.retrieveLatest(base, symbols));
  }

  @GetMapping("/retrieveHistoricalWithBaseAndSymbolsAndDate")
  @ResponseBody
  public ResponseEntity<CurrencyList> retrieveHistoricalWithBaseAndSymbolsAndDate(
      @RequestParam String base, @RequestParam String symbols, @RequestParam String date) {
    return ResponseEntity.ok().body(service.retrieveHistorical(base, symbols, date));
  }

  @GetMapping("/computeChange")
  @ResponseBody
  public ResponseEntity<BigDecimal> computeChange(@RequestParam String base,
      @RequestParam String symbol, @RequestParam String startDate, @RequestParam String endDate) {
    try {
      return ResponseEntity.ok().body(service.computeChange(base, symbol, startDate, endDate));
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/computeConversion")
  @ResponseBody
  public ResponseEntity<BigDecimal> computeConversion(@RequestParam String base,
      @RequestParam String symbol, @RequestParam String amount) {
    return ResponseEntity.ok().body(service.computeConversion(base, symbol, amount));
  }

  @GetMapping("/convert")
  @ResponseBody
  public ResponseEntity<ConvertItem> convert(@RequestParam String from, @RequestParam String to,
      @RequestParam String amount) {
    return ResponseEntity.ok().body(service.convert(from, to, amount));
  }

  @GetMapping("/timeseries")
  @ResponseBody
  public ResponseEntity<TimeseriesList> timeseries(@RequestParam String startDate,
      @RequestParam String endDate) {
    return ResponseEntity.ok().body(service.getTimeSeries(startDate, endDate));
  }

  @GetMapping("/fluctuation")
  @ResponseBody
  public ResponseEntity<FluctuationList> fluctuation(@RequestParam String startDate,
      @RequestParam String endDate) {
    return ResponseEntity.ok().body(service.getFluctuation(startDate, endDate));
  }

  @GetMapping("/symbols")
  @ResponseBody
  public ResponseEntity<List<SymbolItem>> symbols() {
    return ResponseEntity.ok().body(service.getSymbols());
  }

  @GetMapping("/generateLatestReport")
  @ResponseBody
  public ResponseEntity<InputStreamResource> generateLatestReport(@RequestParam String base)
      throws FileNotFoundException {

    String currency = FinancialService.validateCurrency(base);

    Path reportPath;

    try {


      reportPath = service.generateLatestReport("currencies", currency);

      Path fileName = reportPath.getFileName();

      if (fileName == null) {
        log.error("Generated report path has no filename: {}", reportPath);
        return ResponseEntity.internalServerError().build();
      }

      String contentDisposition = ContentDisposition.attachment()
          .filename(fileName.toString(), StandardCharsets.UTF_8).build().toString();

      InputStreamResource resource = new InputStreamResource(Files.newInputStream(reportPath));



      return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
          .contentType(MediaType
              .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .contentLength(Files.size(reportPath)).body(resource);

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }

  }

  @GetMapping("/generateHistoricalReport")
  @ResponseBody
  public ResponseEntity<InputStreamResource> generateHistoricalReport(@RequestParam String base,
      @RequestParam String date) throws FileNotFoundException {
    String fileName;
    try {
      fileName = service.generateHistoricalReport("currencies", base, date);
      File file = new File(fileName);

      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + file.getName() + "\"")
          .contentType(MediaType
              .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .contentLength(file.length()).body(resource);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
