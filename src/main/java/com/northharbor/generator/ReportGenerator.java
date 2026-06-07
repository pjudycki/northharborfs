package com.northharbor.generator;

import com.northharbor.model.CurrencyList;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class ReportGenerator {

    private static final String CURR_SHEET_NAME = "Currencies Rates Report";
    private static final String TITLE = "Rates of Exchange by exchangeratesapi.io";
    private static final String DATE = "Date";

    public String generateHistoricalReport(CurrencyList historical, String fileName, String base, String date) throws IOException {
        String resultFileName = createFileName(fileName + "_" + base, date);
        Map<String, BigDecimal> currToRateOfExchange = convertToCurrency(historical, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);

        return resultFileName;
    }

    public String generateLatestReport(CurrencyList latest, String fileName, String base) throws IOException {
        String resultFileName = createFileName(fileName + "_" + base, LocalDate.now().toString());
        Map<String, BigDecimal> currToRateOfExchange = convertToCurrency(latest, base);
        generateExcelReport(resultFileName, currToRateOfExchange, base);
        return resultFileName;
    }

    public void generateExcelReport(String fileName, Map<String, BigDecimal> currToRateOfExchange, String base) throws IOException {
        XSSFWorkbook guaranaFsWorkbook = new XSSFWorkbook();
        XSSFSheet currenciesSheet = guaranaFsWorkbook.createSheet(CURR_SHEET_NAME);

        int rCounter = 0;
        int cCounter = 0;

        FileOutputStream fos = new FileOutputStream(fileName);
        Row firstRow = currenciesSheet.createRow(rCounter++);

        Cell firstCell = firstRow.createCell(cCounter);
        firstCell.setCellValue(TITLE);

        Row secondRow = currenciesSheet.createRow(rCounter++);

        LocalDateTime snapshotDate = LocalDateTime.now();
        Cell secondCell = secondRow.createCell(cCounter++);
        secondCell.setCellValue(DATE);

        Cell thirdCell = secondRow.createCell(cCounter++);
        thirdCell.setCellValue(snapshotDate.toString());
        cCounter = 0;

        Iterator<String> fields = currToRateOfExchange.keySet().iterator();
        try {
            while (fields.hasNext()) {
                String name = fields.next();
                BigDecimal value = currToRateOfExchange.get(name);
                Row row = currenciesSheet.createRow(rCounter++);
                addValuesToRow(name, value, row, cCounter, base);
            }
            guaranaFsWorkbook.write(fos);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            fos.flush();
            fos.close();
        }
    }

    private void addValuesToRow(String name, BigDecimal value, Row row, int cCounter, String base) {
        Cell baseCell = row.createCell(cCounter++);
        baseCell.setCellValue(base);
        Cell nameCells = row.createCell(cCounter++);
        nameCells.setCellValue(name);
        Cell valueCells = row.createCell(cCounter++);
        valueCells.setCellValue(value.doubleValue());
    }

    private Map<String, BigDecimal> convertToCurrency(CurrencyList allRates, String currency) {
        Map<String, BigDecimal> rates = allRates.getRates();
        BigDecimal rateOfExchange = rates.get(currency);

        for (Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
            String name = entry.getKey();
            BigDecimal value = entry.getValue();
            BigDecimal converted = value.divide(rateOfExchange, 6, RoundingMode.HALF_UP);
            rates.put(name, converted);
        }

        return rates;
    }

    private String createFileName(String fileName, String date) {
        StringBuilder result = new StringBuilder(fileName);
        result.append("_");
        result.append(date);
        result.append(".xlsx");
        return result.toString();
    }
}
