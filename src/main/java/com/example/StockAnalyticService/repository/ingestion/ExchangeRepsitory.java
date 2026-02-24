package com.example.StockAnalyticService.repository.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.Exchange;


interface ExchangeRepsitory extends JpaRepository<Exchange,Long> {
    
}
