package com.northharbor.factory;

import com.northharbor.entity.CurrencyEntity;
import com.northharbor.entity.RetrievalEntity;
import com.northharbor.model.CurrencyList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyFactory {

    public static List<CurrencyEntity> toCurrencyEntity(CurrencyList currencyList, RetrievalEntity retrieval) {
        Map<String, BigDecimal> rates = currencyList.getRates();
        List<CurrencyEntity> result = new ArrayList<>();


        for(Map.Entry<String, BigDecimal> entry : rates.entrySet()) {
            String name = entry.getKey();
            BigDecimal value = entry.getValue();

            CurrencyEntity currency = new CurrencyEntity();
            currency.setCurrencyName(name);
            currency.setRateOfExchange(value);
            currency.setRetrievalId(retrieval);
            result.add(currency);
        }

        return result;
    }

}
