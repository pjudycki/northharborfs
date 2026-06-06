package com.northharbor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class TimeseriesList {

    private boolean success;

    private boolean timeseries;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    private String base;

    private Map<LocalDate, Map<String, Double>> rates;

}
