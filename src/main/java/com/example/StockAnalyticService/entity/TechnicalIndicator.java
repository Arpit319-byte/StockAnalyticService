package com.example.StockAnalyticService.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "technical_indicators")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TechnicalIndicator extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(precision = 19, scale = 6)
    private BigDecimal sma20;

    @Column(precision = 19, scale = 6)
    private BigDecimal sma50;

    @Column(precision = 19, scale = 6)
    private BigDecimal sma200;

    @Column(precision = 19, scale = 6)
    private BigDecimal ema20;

    @Column(precision = 19, scale = 6)
    private BigDecimal ema50;

    @Column(precision = 19, scale = 6)
    private BigDecimal ema200;

    @Column(precision = 19, scale = 6)
    private BigDecimal rsi;

    @Column(precision = 19, scale = 6)
    private BigDecimal macd;

    @Column(precision = 19, scale = 6)
    private BigDecimal macdSignal;

    @Column(precision = 19, scale = 6)
    private BigDecimal bollingerUpper;

    @Column(precision = 19, scale = 6)
    private BigDecimal bollingerLower;

    @Column(precision = 19, scale = 6)
    private BigDecimal volatility;
}
