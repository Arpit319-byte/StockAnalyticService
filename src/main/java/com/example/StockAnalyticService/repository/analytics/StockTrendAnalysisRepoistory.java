package com.example.StockAnalyticService.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.StockTrendAnalysis;

public interface StockTrendAnalysisRepoistory extends JpaRepository<StockTrendAnalysis,Long> {
    
}
