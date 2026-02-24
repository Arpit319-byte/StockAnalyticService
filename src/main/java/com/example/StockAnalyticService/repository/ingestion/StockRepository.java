package com.example.StockAnalyticService.repository.ingestion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.Stock;

interface StockRepository extends JpaRepository<Stock,Long> {
    
    Optional<Stock> findBySymbol(String symbol);

    List<Stock> findByIsActiveTrue();
    
}
