package com.northharbor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "retrieval")
public class RetrievalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private Boolean success;
    @Column
    private Boolean historical;
    @Column
    private Long timestamp;
    @Column
    private LocalDate date;
    @Column
    private String base;

    public void setId(Long id) {
        this.id = id;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setHistorical(Boolean historical) {
        this.historical = historical;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Long getId() {
        return id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getHistorical() {
        return historical;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getBase() {
        return base;
    }

}
