package com.example.StockAnalyticService.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.StockAnalyticService.entity.StockRecommendation;

interface StockRecommendationRepository extends JpaRepository<StockRecommendation,Long>{
    
}
