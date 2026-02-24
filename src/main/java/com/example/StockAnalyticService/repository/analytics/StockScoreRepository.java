package com.example.StockAnalyticService.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.StockScore;

interface StockScoreRepository extends JpaRepository<StockScore,Long> {
    
}
