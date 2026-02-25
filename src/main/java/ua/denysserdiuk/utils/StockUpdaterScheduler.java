package denysserdiuk.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import denysserdiuk.model.Shares;
import denysserdiuk.model.StockPrice;
import denysserdiuk.repository.SharesRepository;
import denysserdiuk.repository.StockPriceRepository;
import denysserdiuk.service.SharesServiceImpl;
import denysserdiuk.service.StockPriceService;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class StockUpdaterScheduler {

    private final SharesRepository sharesRepository;
    private final SharesServiceImpl sharesServiceimpl;
    private final StockPriceService stockPriceService;
    private final StockPriceRepository stockPriceRepository;

    @Autowired
    public StockUpdaterScheduler(SharesRepository sharesRepository,
                                 SharesServiceImpl sharesServiceimpl,
                                 StockPriceService stockPriceService,
                                 StockPriceRepository stockPriceRepository) {
        this.sharesRepository = sharesRepository;
        this.sharesServiceimpl = sharesServiceimpl;
        this.stockPriceService = stockPriceService;
        this.stockPriceRepository = stockPriceRepository;
    }

    // This will run every 5 minutes, 24/7
    @Scheduled(cron = "0 0/5 16-18 * * MON-FRI", zone = "America/New_York")
    public void fetchStockPrices() {
        updateStockPrices();
    }

    //find and update all stock prices
    private void updateStockPrices() {
        List<Shares> sharesList = sharesRepository.findAll();

        for (Shares share : sharesList) {
            StockPrice stockPrice = stockPriceRepository.findByTicker(share.getTicker());

            // Only update stock prices if they were last updated more than 24 hours ago
            if (stockPrice == null || stockPrice.getLastUpdated().isBefore(LocalDateTime.now().minusHours(24))) {
                stockPriceService.updateStockPrice(share.getTicker());

                // Recalculate profit after updating the stock price
                sharesServiceimpl.updateShareProfit(share, stockPrice);
            }
        }
    }
}
