package com.example.StockAnalyticService.repository.ingestion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.StockAnalyticService.entity.PriceInterval;
import com.example.StockAnalyticService.entity.Stock;
import com.example.StockAnalyticService.entity.StockPrice;

public interface StockPriceRepository extends JpaRepository<StockPrice,Long>{

    public List<StockPrice> findByStockAndIntervalOrderByTimestampAsc(Stock stock,PriceInterval interval);
    
}
