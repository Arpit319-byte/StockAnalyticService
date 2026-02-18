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
@Table(name = "stock_recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRecommendation extends BaseModel{



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false, unique = true)
    private Stock stock;

    @Column(nullable = false)
    private String recommendation;
    // BUY, SELL, HOLD

    @Column(nullable = false)
    private Double confidence;
    // 0 to 100

    private String reason;
    // explanation

    @Column(precision = 19, scale = 6)
    private BigDecimal targetPrice;

    @Column(precision = 19, scale = 6)
    private BigDecimal stopLoss;

    @Column(precision = 19, scale = 6)
    private BigDecimal riskRewardRatio;
    
}
