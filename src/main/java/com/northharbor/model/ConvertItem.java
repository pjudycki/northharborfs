package com.northharbor.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ConvertItem {

    private boolean success;
    private ExchangeItem query;
    private InfoItem info;
    private String historical;
    private LocalDate date;
    private Double result;

}
