package com.northharbor.entity;


import java.math.BigDecimal;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
      justification = "JPA relationship stores the managed entity reference")
  public void setRetrievalId(RetrievalEntity retrievalId) {
    this.retrievalId = retrievalId;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP",
      justification = "JPA relationship exposes the managed entity reference")
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
