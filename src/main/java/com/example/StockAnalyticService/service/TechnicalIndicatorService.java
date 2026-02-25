package com.example.StockAnalyticService.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import com.example.StockAnalyticService.repository.analytics.TechnicalIndicatorRepository;
import com.example.StockAnalyticService.repository.ingestion.StockPriceRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TechnicalIndicatorService {


    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 6;


    private final StockPriceRepository  stockPriceRepository;
    private final TechnicalIndicatorRepository technicalIndicatorRepository;

    public TechnicalIndicatorService(StockPriceRepository stockPriceRepository, TechnicalIndicatorRepository technicalIndicatorRepository){
        this.stockPriceRepository=stockPriceRepository;
        this.technicalIndicatorRepository=technicalIndicatorRepository;
    }




    private BigDecimal computeSMA(List<BigDecimal> closes,int period){

        if(closes.size()-1 < period){
            log.info("Closes size is less than the period ");
            return null;
        }
    
        BigDecimal sum=BigDecimal.ZERO;
        for(int i=0;i<closes.size();i++){
            sum=sum.add(closes.get(i));
        }
        return  sum.divide(BigDecimal.valueOf(period), MC).setScale(SCALE,RoundingMode.HALF_UP);
    }

    
}
