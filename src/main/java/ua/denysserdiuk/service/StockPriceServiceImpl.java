package denysserdiuk.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import denysserdiuk.model.StockPrice;
import denysserdiuk.repository.StockPriceRepository;

import java.time.LocalDateTime;

@Service
public class StockPriceServiceImpl implements StockPriceService {

    @Value("${polygon.io.api.key}")
    private String apiKey;

    private final StockPriceRepository stockPriceRepository;

    @Autowired
    public StockPriceServiceImpl(StockPriceRepository stockPriceRepository){
        this.stockPriceRepository = stockPriceRepository;
    }


    @Override
    public void updateStockPrice(String ticker) {
        // Fetch the stock price for the given ticker from the database
        StockPrice stockPrice = stockPriceRepository.findByTicker(ticker);

        // Fetch stock price from Polygon.io API
        String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/prev?adjusted=true&apiKey=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = new JSONObject(result);

            // Extract the "results" array and get the first item (previous close value)
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            JSONObject lastResult = resultsArray.getJSONObject(0);

            // Get the closing price (key "c")
            double price = lastResult.getDouble("c");
            System.out.println("Fetched closing price for ticker " + ticker + ": " + price);

            // If no stock price exists, create a new entry; otherwise, update the existing entry
            if (stockPrice == null) {
                stockPrice = new StockPrice(ticker, price);
                System.out.println("Adding new stock price for ticker " + ticker);
            } else {
                stockPrice.setPrice(price);
                stockPrice.setLastUpdated(LocalDateTime.now());
                System.out.println("Updating stock price for ticker " + ticker);
            }

            // Save the updated/new stock price to the database
            stockPriceRepository.save(stockPrice);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public StockPrice getStockPrice(String ticker) {
        // Fetch the stock price for the given ticker from the database
        return stockPriceRepository.findByTicker(ticker);
    }
}
