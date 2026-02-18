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

    @Column(name = "recommendation", nullable = false)
    private String recommendation;

    @Column(name = "confidence", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidence;

    @Column(name = "reason")
    private String reason;
    // explanation

    @Column(name = "target_price", precision = 19, scale = 6)
    private BigDecimal targetPrice;

    @Column(name = "stop_loss", precision = 19, scale = 6)
    private BigDecimal stopLoss;

    @Column(name = "risk_reward_ratio", precision = 19, scale = 6)
    private BigDecimal riskRewardRatio;
    
}
