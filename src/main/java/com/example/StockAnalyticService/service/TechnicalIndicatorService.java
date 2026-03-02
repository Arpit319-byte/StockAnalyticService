package com.example.StockAnalyticService.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.StockAnalyticService.entity.PriceInterval;
import com.example.StockAnalyticService.entity.Stock;
import com.example.StockAnalyticService.entity.StockPrice;
import com.example.StockAnalyticService.entity.TechnicalIndicator;
import com.example.StockAnalyticService.repository.analytics.TechnicalIndicatorRepository;
import com.example.StockAnalyticService.repository.ingestion.StockPriceRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TechnicalIndicatorService {


    private static final int MIN_DATA_REQUIRED = 200;
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 6;


    private final StockPriceRepository  stockPriceRepository;
    private final TechnicalIndicatorRepository technicalIndicatorRepository;

    public TechnicalIndicatorService(StockPriceRepository stockPriceRepository, TechnicalIndicatorRepository technicalIndicatorRepository){
        this.stockPriceRepository=stockPriceRepository;
        this.technicalIndicatorRepository=technicalIndicatorRepository;
    }


    @Transactional
    public Optional<TechnicalIndicator> computeAndSave(Stock stock,LocalDate date){

        List<StockPrice> prices=stockPriceRepository.findByStockAndIntervalOrderByTimestampAsc(stock,PriceInterval.ONE_DAY);
        
        if(prices == null ||  prices.size() < MIN_DATA_REQUIRED){   
            log.info("Size of list of the price that is being fetched null or less than the required MIN_DATA_REQUIRED");
            return Optional.empty();
        }

        List<BigDecimal> closes=prices.stream()
                                .map(StockPrice::getClose)
                                .toList();

        LocalDate latestDate=prices.get(prices.size()-1).getTimestamp()
                             .atZone(ZoneId.systemDefault())
                             .toLocalDate(); 
                             
        TechnicalIndicator indicator=technicalIndicatorRepository
                                    .findByStockAndDate(stock,latestDate)
                                    .orElse(new TechnicalIndicator());


        indicator.setStock(stock);
        indicator.setDate(latestDate);

        indicator.setSma20(computeSMA(closes,20));
        indicator.setSma50(computeSMA(closes,50));
        indicator.setSma200(computeSMA(closes,200));

        return Optional.of(technicalIndicatorRepository.save(indicator));
               
    }







    private BigDecimal computeSMA(List<BigDecimal> closes,int period){

        if(closes.size()< period){
            log.info("Closes size is less than the period ");
            return null;
        }
    
        BigDecimal sum=BigDecimal.ZERO;
        for(int i=closes.size()-period;i<closes.size();i++){
            sum=sum.add(closes.get(i));
        }
        return  sum.divide(BigDecimal.valueOf(period), MC).setScale(SCALE,RoundingMode.HALF_UP);
    }

    
}
