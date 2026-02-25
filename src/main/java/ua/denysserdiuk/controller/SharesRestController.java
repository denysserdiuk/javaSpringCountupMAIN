package denysserdiuk.controller;

import denysserdiuk.dto.StockPriceDto; // Import DTO
import denysserdiuk.model.StockPrice;
import denysserdiuk.service.StockPriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SharesRestController {
    private final StockPriceService stockPriceService;

    public SharesRestController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/stock-price")
    public ResponseEntity<StockPriceDto> getStockPrice(@RequestParam String ticker) {
        StockPrice price = stockPriceService.getStockPrice(ticker);

        if (price == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Map Entity to DTO
        StockPriceDto dto = new StockPriceDto(price.getTicker(), price.getPrice());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}