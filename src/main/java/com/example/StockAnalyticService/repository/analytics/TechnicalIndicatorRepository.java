package com.example.StockAnalyticService.repository.analytics;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StockAnalyticService.entity.Stock;
import com.example.StockAnalyticService.entity.TechnicalIndicator;

public interface TechnicalIndicatorRepository extends JpaRepository<TechnicalIndicator,Long> {

    Optional<TechnicalIndicator> findByStockAndDate(Stock stock,LocalDate date);
    
}
