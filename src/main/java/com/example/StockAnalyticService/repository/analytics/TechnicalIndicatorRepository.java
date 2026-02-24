package com.example.StockAnalyticService.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.TechnicalIndicator;

interface TechnicalIndicatorRepository extends JpaRepository<TechnicalIndicator,Long> {
    
}
