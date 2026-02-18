package com.example.StockAnalyticService.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_trend_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTrendAnalysis extends BaseModel{

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="stock_id",nullable = false,unique = true)
    private Stock stock;

    @Column(nullable = false)
    private String trend;
    // BULLISH, BEARISH, SIDEWAYS

    private String momentum;
    // STRONG, WEAK, NEUTRAL
    
    @Column(precision = 19, scale = 6)
    private BigDecimal supportLevel;

    @Column(precision = 19, scale = 6)
    private BigDecimal resistanceLevel;

    @Column(precision = 19, scale = 6)
    private BigDecimal breakoutSignal;

    @Column(precision = 19, scale = 6)
    private BigDecimal volatility;

    @Column(precision = 19, scale = 6)
    private BigDecimal trendStrength;
    
}
