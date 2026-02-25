package denysserdiuk.dto;

public class StockPriceDto {
    private String ticker;
    private Double price;

    // Constructors
    public StockPriceDto(String ticker, Double price) {
        this.ticker = ticker;
        this.price = price;
    }

    // Getters...
    public String getTicker() {
        return ticker;
    }

    public Double getPrice() {
        return price;
    }
}