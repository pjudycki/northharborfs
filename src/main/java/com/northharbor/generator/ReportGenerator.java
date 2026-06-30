package com.northharbor.generator;

import com.northharbor.model.CurrencyList;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class ReportGenerator {

	private static final String CURR_SHEET_NAME = "Currencies Rates Report";
	private static final String TITLE = "Rates of Exchange by exchangeratesapi.io";
	private static final String DATE = "Date";
	private static final Path REPORT_DIRECTORY = Paths.get("reports").toAbsolutePath().normalize();
	private static final String TIME_ZONE = "UTC";

	public String generateHistoricalReport(CurrencyList historical, String fileName, String base, String date)
			throws IOException {

		String resultFileName = createFileName(fileName + "_" + base, date);
		Files.createDirectories(REPORT_DIRECTORY);
		Path reportPath = REPORT_DIRECTORY.resolve(resultFileName).normalize();

		if (!reportPath.startsWith(REPORT_DIRECTORY)) {
			throw new SecurityException("Invalid report path");
		}

		Map<String, BigDecimal> currToRateOfExchange = convertToCurrency(historical, base);
		generateExcelReport(reportPath, currToRateOfExchange, base);

		return resultFileName;
	}

	public Path generateLatestReport(CurrencyList latest, String fileName, String base) throws IOException {

		String resultFileName = createFileName(fileName + "_" + base, LocalDate.now(ZoneId.of(TIME_ZONE)).toString());
		Files.createDirectories(REPORT_DIRECTORY);
		Path reportPath = REPORT_DIRECTORY.resolve(resultFileName).normalize();

		if (!reportPath.startsWith(REPORT_DIRECTORY)) {
			throw new SecurityException("Invalid report path");
		}

		Map<String, BigDecimal> currToRateOfExchange = convertToCurrency(latest, base);
		generateExcelReport(reportPath, currToRateOfExchange, base);
		return reportPath;
	}

	public void generateExcelReport(Path reportPath, Map<String, BigDecimal> currToRateOfExchange, String base)
			throws IOException {
		XSSFWorkbook northharborFsWorkbook = new XSSFWorkbook();
		XSSFSheet currenciesSheet = northharborFsWorkbook.createSheet(CURR_SHEET_NAME);

		int rCounter = 0;
		int cCounter = 0;

		OutputStream fos = Files.newOutputStream(reportPath);
		Row firstRow = currenciesSheet.createRow(rCounter++);

		Cell firstCell = firstRow.createCell(cCounter);
		firstCell.setCellValue(TITLE);

		Row secondRow = currenciesSheet.createRow(rCounter++);

		LocalDateTime snapshotDate = LocalDateTime.now(ZoneId.of("UTC"));
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
			northharborFsWorkbook.write(fos);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			northharborFsWorkbook.close();
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
