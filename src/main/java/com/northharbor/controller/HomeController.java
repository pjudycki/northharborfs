package com.northharbor.controller;


import com.northharbor.service.FinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RestController
public class HomeController {

    @Autowired
    private FinancialService financialService;

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/viewSymbols")
    public ModelAndView viewBooks(Model model) {
        model.addAttribute("symbolList", financialService.getSymbols());
        return new ModelAndView("view-symbols");
    }

    @GetMapping("/viewLatest")
    public ModelAndView viewLatest(Model model, @RequestParam(required = false, defaultValue = "EUR") String currency) {
        model.addAttribute("latestList", financialService.retrieveLatestAsCurrencyItem(currency));
        model.addAttribute("currencies", Arrays.asList("EUR", "USD", "CHF", "GBP"));
        return new ModelAndView("view-latest");
    }

    @GetMapping("/viewHistorical")
    public ModelAndView viewHistorical(Model model) {
        model.addAttribute("historicalList", financialService.retrieveHistoricalAsCurrencyItem());
        return new ModelAndView("view-historical");
    }
}