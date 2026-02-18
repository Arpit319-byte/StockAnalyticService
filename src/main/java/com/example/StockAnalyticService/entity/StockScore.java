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
@Table(name = "stock_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockScore extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false, unique = true)
    private Stock stock;

    @Column(name = "technical_score", precision = 19, scale = 6)
    private BigDecimal technicalScore;

    @Column(name = "momentum_score", precision = 19, scale = 6)
    private BigDecimal momentumScore;

    @Column(name = "volatility_score", precision = 19, scale = 6)
    private BigDecimal volatilityScore;

    @Column(name = "trend_score", precision = 19, scale = 6)
    private BigDecimal trendScore;

    @Column(name = "overall_score", precision = 19, scale = 6)
    private BigDecimal overallScore;
}
