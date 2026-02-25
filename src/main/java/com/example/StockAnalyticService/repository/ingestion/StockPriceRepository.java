package com.example.StockAnalyticService.repository.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice,Long>{
    
}
