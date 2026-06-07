package com.northharbor.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "currency")
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "rate_of_exchange")
    private BigDecimal rateOfExchange;

    @ManyToOne
    @JoinColumn(name = "retrieval_id")
    private RetrievalEntity retrievalId;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public void setRateOfExchange(BigDecimal rateOfExchange) {
        this.rateOfExchange = rateOfExchange;
    }

    public void setRetrievalId(RetrievalEntity retrievalId) {
        this.retrievalId = retrievalId;
    }

    public RetrievalEntity getRetrievalId() {
        return retrievalId;
    }

    public BigDecimal getRateOfExchange() {
        return rateOfExchange;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
