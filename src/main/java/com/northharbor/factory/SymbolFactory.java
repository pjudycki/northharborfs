package com.northharbor.factory;

import com.northharbor.entity.SymbolEntity;
import com.northharbor.model.SymbolItem;

public class SymbolFactory {

    public static SymbolEntity createSymbolEntity(SymbolItem symbolItem) {
        SymbolEntity symbolEntity = new SymbolEntity();
        symbolEntity.setCurrencyCode(symbolItem.getCurrencyCode());
        symbolEntity.setCurrencyName(symbolItem.getCurrencyName());
        return symbolEntity;
    }

    public static SymbolItem createSymbolItem(SymbolEntity symbolEntity) {
        return SymbolItem.builder()
                .currencyCode(symbolEntity.getCurrencyCode())
                .currencyName(symbolEntity.getCurrencyName()).build();
    }
}
