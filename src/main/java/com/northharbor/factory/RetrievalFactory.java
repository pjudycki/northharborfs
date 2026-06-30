package com.northharbor.factory;

import com.northharbor.entity.RetrievalEntity;
import com.northharbor.model.CurrencyList;

public class RetrievalFactory {
	
	private RetrievalFactory() {
		
	}

    public static RetrievalEntity toRetrievalEntity(CurrencyList currencyList) {
        RetrievalEntity retrievalEntity = new RetrievalEntity();
        retrievalEntity.setBase(currencyList.getBase());
        retrievalEntity.setHistorical(currencyList.isHistorical());
        retrievalEntity.setSuccess(currencyList.isSuccess());
        retrievalEntity.setDate(currencyList.getDate());
        retrievalEntity.setTimestamp(currencyList.getTimestamp());
        return retrievalEntity;
    }
}
