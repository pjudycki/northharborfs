package com.northharbor.repository;

import com.northharbor.entity.CurrencyEntity;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<CurrencyEntity, Long> {
}
