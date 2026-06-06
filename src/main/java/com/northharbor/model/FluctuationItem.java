package com.northharbor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FluctuationItem {
    @JsonProperty("start_rate")
    private Double startRate;
    @JsonProperty("end_rate")
    private Double endRate;
    private Double change;
    @JsonProperty("change_pct")
    private Double changePct;
}
