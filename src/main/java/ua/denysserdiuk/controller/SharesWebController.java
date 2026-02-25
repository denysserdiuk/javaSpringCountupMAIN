package denysserdiuk.controller;

import denysserdiuk.dto.SharesDisplayDto;
import denysserdiuk.dto.SharesSubmissionDto;
import denysserdiuk.model.Budget;
import denysserdiuk.model.Shares;
import denysserdiuk.model.StockPrice;
import denysserdiuk.model.Users;
import denysserdiuk.repository.SharesRepository;
import denysserdiuk.repository.StockPriceRepository;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetService;
import denysserdiuk.service.SharesService;
import denysserdiuk.service.StockPriceService;
import denysserdiuk.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class SharesWebController {
    // ... dependencies (same as before) ...
    private final UserRepository userRepository;
    private final SharesService sharesService;
    private final StockPriceRepository stockPriceRepository;
    private final StockPriceService stockPriceService;
    private final BudgetService budgetService;
    private final SharesRepository sharesRepository;

    // Constructor injection (same as before)
    public SharesWebController(UserRepository userRepository, SharesService sharesService, StockPriceRepository stockPriceRepository, StockPriceService stockPriceService, BudgetService budgetService, SharesRepository sharesRepository) {
        this.userRepository = userRepository;
        this.sharesService = sharesService;
        this.stockPriceRepository = stockPriceRepository;
        this.stockPriceService = stockPriceService;
        this.budgetService = budgetService;
        this.sharesRepository = sharesRepository;
    }

    @PostMapping("/addShare")
    public String addShare(SharesSubmissionDto shareDto, Model model) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        // 1. Validation Logic
        if (shareDto.getTicker() == null || shareDto.getAmount() == null) {
            model.addAttribute("error", "Invalid share data");
            return "shares";
        }

        // 2. Fetch/Update Stock Price Logic
        StockPrice existingStockPrice = stockPriceRepository.findByTicker(shareDto.getTicker());
        if (existingStockPrice == null) {
            stockPriceService.updateStockPrice(shareDto.getTicker());
            existingStockPrice = stockPriceRepository.findByTicker(shareDto.getTicker());
        }

        // 3. Map DTO to Entity Logic
        // We handle the merge logic here (checking if share exists)

        Optional<Shares> existingShareOpt = Optional.ofNullable(sharesRepository.findByTickerAndUserId(shareDto.getTicker(), user));

        if (existingShareOpt.isPresent()) {
            // -- UPDATE EXISTING SHARE --
            Shares shareToUpdate = existingShareOpt.get();

            // Calculate new average price (Weighted Average)
            double totalValue = (shareToUpdate.getAmount() * shareToUpdate.getPrice())
                    + (shareDto.getAmount() * shareDto.getPrice());
            double newTotalAmount = shareToUpdate.getAmount() + shareDto.getAmount();

            shareToUpdate.setAmount(newTotalAmount);
            shareToUpdate.setPrice((double) Math.round(totalValue / newTotalAmount * 100) / 100);

            // Recalculate profit
            shareToUpdate.setProfit(sharesService.updateShareProfit(shareToUpdate, existingStockPrice));

            sharesRepository.save(shareToUpdate);
            model.addAttribute("message", "Share amount updated");
        } else {
            // -- CREATE NEW SHARE --
            Shares newShare = new Shares();
            newShare.setTicker(shareDto.getTicker());
            newShare.setAmount(shareDto.getAmount());
            newShare.setPrice(shareDto.getPrice());
            newShare.setPurchaseDate(shareDto.getPurchaseDate());
            newShare.setUser(user);

            // Calculate initial profit
            double profit = sharesService.updateShareProfit(newShare, existingStockPrice);
            newShare.setProfit(profit);

            sharesService.addShare(newShare);

            // Create Budget Log
            Budget budget = new Budget();
            budget.setUser(user);
            budget.setDescription("Bought " + newShare.getTicker()); // Better description
            budget.setCategory("Shares");
            budget.setDate(newShare.getPurchaseDate());
            // Standard accounting: Adding a share is an expenditure (loss of cash), creating an Asset.
            // Marking it as "loss" in budget cashflow is technically correct if tracking cash.
            budget.setType("loss");
            budget.setAmount(newShare.getPrice() * newShare.getAmount()); // Amount spent

            budgetService.addBudgetLine(budget);
            model.addAttribute("message", "New share saved");
        }

        return "shares";
    }

    @GetMapping("/user-shares")
    public ResponseEntity<List<SharesDisplayDto>> getUserShares() {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);
        List<Shares> userShares = sharesService.findByUserId(user.getId());

        // Map Entities to DTOs
        List<SharesDisplayDto> dtos = new ArrayList<>();
        for (Shares share : userShares) {
            SharesDisplayDto dto = new SharesDisplayDto();
            dto.setId(share.getId());
            dto.setTicker(share.getTicker());
            dto.setAmount(share.getAmount());
            dto.setPrice(share.getPrice());
            dto.setProfit(share.getProfit());
            dto.setPurchaseDate(share.getPurchaseDate());
            dtos.add(dto);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}