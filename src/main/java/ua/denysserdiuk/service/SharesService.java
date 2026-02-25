package denysserdiuk.service;

import denysserdiuk.model.Shares;
import denysserdiuk.model.StockPrice;
import java.util.List;

public interface SharesService {
    String addShare(Shares share);
    List<Shares> findByUserId(long userId);
    Double updateShareProfit(Shares share, StockPrice stockprice);
    void deleteShare(Shares share);
}
