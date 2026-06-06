package com.northharbor.controller;

import com.northharbor.model.CurrencyList;
import com.northharbor.service.FinancialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/appname")
    public String appname() {
        return "North Harbor Financial Services";
    }
}
