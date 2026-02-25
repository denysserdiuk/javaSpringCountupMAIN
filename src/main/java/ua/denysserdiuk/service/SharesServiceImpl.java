package denysserdiuk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import denysserdiuk.model.Shares;
import denysserdiuk.model.StockPrice;
import denysserdiuk.model.Users;
import denysserdiuk.repository.SharesRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SharesServiceImpl implements SharesService {
    private final SharesRepository sharesRepository;

    @Autowired
    public SharesServiceImpl(SharesRepository sharesRepository) {
        this.sharesRepository = sharesRepository;
    }

    @Override
    public String addShare(Shares share) {
        Shares share1 = new Shares();

        share1.setUser(share.getUser());
        share1.setTicker(share.getTicker());
        share1.setAmount(share.getAmount());
        share1.setPrice(share.getPrice());
        share1.setPurchaseDate(share.getPurchaseDate());
        share1.setProfit(share.getProfit());
        sharesRepository.save(share1);

        return "Share added";
    }

    @Override
    public List<Shares> findByUserId(long userId) {
        return sharesRepository.findByUserId(userId);
    }

    @Override
    public Double updateShareProfit(Shares share, StockPrice stockprice) {
        BigDecimal purchasePrice = BigDecimal.valueOf(share.getPrice());
        BigDecimal currentPrice = BigDecimal.valueOf(stockprice.getPrice());
        BigDecimal amount = BigDecimal.valueOf(share.getAmount());

        BigDecimal profit = currentPrice.subtract(purchasePrice).multiply(amount);

        profit = profit.setScale(2, RoundingMode.HALF_UP);

        return profit.doubleValue();
    }

    @Override
    public void deleteShare(Shares share) {
        sharesRepository.delete(share);
    }
}
