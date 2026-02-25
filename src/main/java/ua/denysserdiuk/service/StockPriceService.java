package denysserdiuk.service;

import denysserdiuk.model.StockPrice;

import java.util.List;

public interface StockPriceService {
    StockPrice getStockPrice(String ticker);  // Return single StockPrice
    void updateStockPrice(String ticker);
}

