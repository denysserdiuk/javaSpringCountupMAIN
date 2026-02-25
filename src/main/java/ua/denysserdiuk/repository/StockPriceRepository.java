package denysserdiuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import denysserdiuk.model.StockPrice;


public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    StockPrice findByTicker(String ticker);
}
