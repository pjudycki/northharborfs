package com.northharbor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.northharbor.entity.CurrencyEntity;
import com.northharbor.entity.RetrievalEntity;
import com.northharbor.entity.SymbolEntity;
import com.northharbor.factory.CurrencyFactory;
import com.northharbor.factory.RetrievalFactory;
import com.northharbor.factory.SymbolFactory;
import com.northharbor.model.CurrencyList;
import com.northharbor.model.SymbolItem;
import com.northharbor.repository.CurrencyRepository;
import com.northharbor.repository.RetrievalRepository;
import com.northharbor.repository.SymbolRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Slf4j
public class GetExchangeRates {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RetrievalRepository retrievalRepository;

    @Autowired
    private SymbolRepository symbolRepository;

    @PersistenceContext
    private EntityManager entityManager;


    //@Scheduled(fixedDelay = 60000)
    @PostConstruct
    public void processAndSave() {
        CurrencyList result = financialService.retrieveLatest("EUR", null);
        processAndSaveForCurrency(result);
/*        result = financialService.retrieveLatest("USD", null);
        processAndSaveForCurrency(result);
        result = financialService.retrieveLatest("GBP", null);
        processAndSaveForCurrency(result);
        result = financialService.retrieveLatest("CHF", null);
        processAndSaveForCurrency(result);
    */

    }

    //@PostConstruct
    public void initializeSymbolsTable() {
        List<SymbolItem> symbolItems = financialService.getSymbols();
        List<SymbolEntity> symbols =
                symbolItems.stream().map(SymbolFactory::createSymbolEntity).collect(Collectors.toList());
        symbolRepository.saveAll(symbols);
    }

    //@PostConstruct
    public void processAndSaveHistorical() {
       Query query = entityManager.createQuery("SELECT re FROM RetrievalEntity re ORDER by re.date DESC");
       query.setMaxResults(1);
       Optional singleResult = query.getResultList().stream().findFirst();
       LocalDate retrievalStartDate = LocalDate.of(2023, 10, 1);

       if (singleResult.isPresent()) {
           RetrievalEntity retrievalEntity = (RetrievalEntity) singleResult.get();
           retrievalStartDate = retrievalEntity.getDate();
       }

       long days = DAYS.between(retrievalStartDate, LocalDate.now());
       long counter = 0L;

       while (counter < days) {
           CurrencyList result = financialService.retrieveHistorical(null, null, retrievalStartDate.toString());
           log.info(String.valueOf(result));
           processAndSaveForCurrency(result);
           counter++;
           retrievalStartDate = retrievalStartDate.plusDays(1);
       }
    }

    private void processAndSaveForCurrency(CurrencyList currencyList) {
        RetrievalEntity retrievalEntity = retrievalRepository.save(RetrievalFactory.toRetrievalEntity(currencyList));
        List<CurrencyEntity> currencyEntities = CurrencyFactory.toCurrencyEntity(currencyList, retrievalEntity);
        currencyRepository.saveAll(currencyEntities);
    }

}
