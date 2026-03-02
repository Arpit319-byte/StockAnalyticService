package com.example.StockAnalyticService.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
    private static final int BOLLINGER_PERIOD = 20;
    private static final BigDecimal BOLLINGER_STD = BigDecimal.valueOf(2);
    private static final int VOLATILITY_PERIOD = 20;
    private static final int RSI_PERIOD = 14;
    private static final int MACD_FAST = 12, MACD_SLOW = 26, MACD_SIGNAL = 9;

    private final StockPriceRepository stockPriceRepository;
    private final TechnicalIndicatorRepository technicalIndicatorRepository;

    public TechnicalIndicatorService(StockPriceRepository stockPriceRepository,
            TechnicalIndicatorRepository technicalIndicatorRepository) {
        this.stockPriceRepository = stockPriceRepository;
        this.technicalIndicatorRepository = technicalIndicatorRepository;
    }

    @Transactional
    public Optional<TechnicalIndicator> computeAndSave(Stock stock, LocalDate date) {

        List<StockPrice> prices = stockPriceRepository.findByStockAndIntervalOrderByTimestampAsc(stock,
                PriceInterval.ONE_DAY);

        if (prices == null || prices.size() < MIN_DATA_REQUIRED) {
            log.info(
                    "Size of list of the price that is being fetched null or less than the required MIN_DATA_REQUIRED");
            return Optional.empty();
        }

        List<BigDecimal> closes = prices.stream()
                .map(StockPrice::getClose)
                .toList();

        LocalDate latestDate = prices.get(prices.size() - 1).getTimestamp()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        TechnicalIndicator indicator = technicalIndicatorRepository
                .findByStockAndDate(stock, latestDate)
                .orElse(new TechnicalIndicator());

        indicator.setStock(stock);
        indicator.setDate(latestDate);

        indicator.setSma20(computeSMA(closes, 20));
        indicator.setSma50(computeSMA(closes, 50));
        indicator.setSma200(computeSMA(closes, 200));

        List<BigDecimal> ema20List = computeEMA(closes, 20);
        List<BigDecimal> ema50List = computeEMA(closes, 50);
        List<BigDecimal> ema200List = computeEMA(closes, 200);
        indicator.setEma20(ema20List.isEmpty() ? null : ema20List.get(ema20List.size() - 1));
        indicator.setEma50(ema50List.isEmpty() ? null : ema50List.get(ema50List.size() - 1));
        indicator.setEma200(ema200List.isEmpty() ? null : ema200List.get(ema200List.size() - 1));

        // RSI
        indicator.setRsi(computeRSI(closes));

        // MACD
        BigDecimal[] macd = computeMACD(closes);
        indicator.setMacd(macd[0]);
        indicator.setMacdSignal(macd[1]);

        // Bollinger Bands ̰ ̰ ̰ ̰ ̰
        BigDecimal[] bollinger = computeBollingerBands(closes);
        indicator.setBollingerUpper(bollinger[0]);
        indicator.setBollingerLower(bollinger[1]);

        // Volatility
        indicator.setVolatility(computeVolatility(closes));

        return Optional.of(technicalIndicatorRepository.save(indicator));

    }

    private BigDecimal computeSMA(List<BigDecimal> closes, int period) {

        if (closes.size() < period) {
            log.info("Closes size is less than the period ");
            return null;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = closes.size() - period; i < closes.size(); i++) {
            sum = sum.add(closes.get(i));
        }
        return sum.divide(BigDecimal.valueOf(period), MC).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private List<BigDecimal> computeEMA(List<BigDecimal> closes, int period) {

        if (closes.size() < period) {
            log.info("Closes size {} is less than the period {}", closes.size(), period);
            return null;
        }

        BigDecimal ema = computeSMA(closes.subList(0, period), period);
        BigDecimal multiplier = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(period + 1), MC);
        List<BigDecimal> emaList = new ArrayList<>();

        if (ema == null)
            return emaList;
        emaList.add(ema);
        for (int i = period; i < closes.size(); i++) {
            ema = closes.get(i).multiply(multiplier).add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
            emaList.add(ema.setScale(SCALE, RoundingMode.HALF_UP));
        }

        return emaList;
    }

    private BigDecimal computeRSI(List<BigDecimal> closes) {
        if (closes.size() < RSI_PERIOD + 1)
            return null;
        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;
        for (int i = closes.size() - RSI_PERIOD; i < closes.size(); i++) {
            BigDecimal change = closes.get(i).subtract(closes.get(i - 1));
            if (change.compareTo(BigDecimal.ZERO) > 0)
                avgGain = avgGain.add(change);
            else
                avgLoss = avgLoss.add(change.abs());
        }
        avgGain = avgGain.divide(BigDecimal.valueOf(RSI_PERIOD), MC);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(RSI_PERIOD), MC);
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.valueOf(100).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal rs = avgGain.divide(avgLoss, MC);
        return BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), MC))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal[] computeMACD(List<BigDecimal> closes) {
        if (closes.size() < MACD_SLOW + MACD_SIGNAL)
            return new BigDecimal[] { null, null };
        List<BigDecimal> emaFast = computeEMA(closes, MACD_FAST);
        List<BigDecimal> emaSlow = computeEMA(closes, MACD_SLOW);
        int startIdx = MACD_SLOW - 1;
        List<BigDecimal> macdLine = new ArrayList<>();
        for (int i = startIdx; i < closes.size(); i++) {
            macdLine.add(emaFast.get(i - MACD_FAST + 1).subtract(emaSlow.get(i - MACD_SLOW + 1)));
        }
        List<BigDecimal> signalLine = computeEMA(macdLine, MACD_SIGNAL);
        BigDecimal macd = macdLine.get(macdLine.size() - 1).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal signal = signalLine.isEmpty() ? null
                : signalLine.get(signalLine.size() - 1).setScale(SCALE, RoundingMode.HALF_UP);
        return new BigDecimal[] { macd, signal };
    }

    private BigDecimal[] computeBollingerBands(List<BigDecimal> closes) {
        if (closes.size() < BOLLINGER_PERIOD)
            return new BigDecimal[] { null, null };
        BigDecimal sma = computeSMA(closes, BOLLINGER_PERIOD);
        if (sma == null)
            return new BigDecimal[] { null, null };
        List<BigDecimal> recent = closes.subList(closes.size() - BOLLINGER_PERIOD, closes.size());
        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal c : recent) {
            BigDecimal diff = c.subtract(sma);
            variance = variance.add(diff.multiply(diff));
        }
        variance = variance.divide(BigDecimal.valueOf(BOLLINGER_PERIOD), MC);
        BigDecimal stdDev = sqrt(variance);
        return new BigDecimal[] {
                sma.add(stdDev.multiply(BOLLINGER_STD)).setScale(SCALE, RoundingMode.HALF_UP),
                sma.subtract(stdDev.multiply(BOLLINGER_STD)).setScale(SCALE, RoundingMode.HALF_UP)
        };
    }

    private BigDecimal computeVolatility(List<BigDecimal> closes) {
        if (closes.size() < VOLATILITY_PERIOD + 1)
            return null;
        List<BigDecimal> returns = new ArrayList<>();
        for (int i = closes.size() - VOLATILITY_PERIOD; i < closes.size(); i++) {
            if (closes.get(i - 1).compareTo(BigDecimal.ZERO) == 0)
                continue;
            returns.add(closes.get(i).subtract(closes.get(i - 1)).divide(closes.get(i - 1), MC));
        }
        if (returns.isEmpty())
            return null;
        BigDecimal mean = returns.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(returns.size()), MC);
        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal r : returns) {
            BigDecimal diff = r.subtract(mean);
            variance = variance.add(diff.multiply(diff));
        }
        variance = variance.divide(BigDecimal.valueOf(returns.size()), MC);
        return sqrt(variance).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        BigDecimal x = value.divide(BigDecimal.valueOf(2), MC);
        for (int i = 0; i < 10; i++)
            x = x.add(value.divide(x, MC)).divide(BigDecimal.valueOf(2), MC);
        return x.setScale(SCALE, RoundingMode.HALF_UP);
    }

}
