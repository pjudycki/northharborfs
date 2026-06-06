package com.northharbor.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CurrencyItem {
    String fromCurrency;
    String toCurrency;
    LocalDate date;
    BigDecimal rate;
}
