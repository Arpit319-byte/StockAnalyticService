package com.example.StockAnalyticService.repository.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.StockPrice;

interface ExchangeRepsitory extends JpaRepository<StockPrice,Long> {
    
}
