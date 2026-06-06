package com.northharbor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class FluctuationList {

    private boolean success;

    private boolean fluctuation;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    private String base;

    Map<String, FluctuationItem> rates;
}
