package denysserdiuk.controller;

import denysserdiuk.model.Users;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetLinesService;
import denysserdiuk.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {
    private final UserRepository userRepository;
    private final BudgetLinesService budgetLinesService;

    public HomeController(UserRepository userRepository, BudgetLinesService budgetLinesService) {
        this.userRepository = userRepository;
        this.budgetLinesService = budgetLinesService;
    }

    @GetMapping("/home-sidebar")
    public String homeSidebar(Model model) {
        addCommonAttributes(model);
        return "home-sidebar";
    }

    @GetMapping("/home")
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("currentDate", today);

        // 1. Get User
        Users user = addCommonAttributes(model);

        // 2. Calculate Financials
        // (Moved the heavy math logic to a helper method below to keep this clean)
        calculateFinancialStats(user, model);

        // 3. Load Categories & Graphs
        List<String> categories = budgetLinesService.getUserCategories(user.getId());
        model.addAttribute("categories", categories);

        // Optional: Ensure budget lines are up to date (e.g. recurring payments)
        budgetLinesService.updateBudgetLinesForUser(user.getId());

        return "home";
    }

    @GetMapping("/shares")
    public String shares() {
        return "shares";
    }

    @GetMapping("/data")
    public String data() {
        return "budget-planner";
    }

    @GetMapping("/404NotFound")
    public String notFound() {
        return "404-not-found";
    }

    @GetMapping("/logout")
    public String logOut() {
        return "login"; // Note: Spring Security usually handles logout automatically, but this serves as the view
    }

    // --- HELPER METHODS ---

    private Users addCommonAttributes(Model model) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);
        model.addAttribute("username", user.getUsername());
        return user;
    }

    private void calculateFinancialStats(Users user, Model model) {
        int currentYear = LocalDate.now().getYear();

        // Fetch Data
        double annualBalance = budgetLinesService.getAnnualBalance(user.getId(), currentYear);
        double lastYearBalance = budgetLinesService.getAnnualBalance(user.getId(), currentYear - 1);
        double monthlyBalance = budgetLinesService.getMonthlyBalance(user.getId(), LocalDate.now().getMonthValue(), currentYear);
        double allTimeBalance = budgetLinesService.getAllTimeBalance(user.getId());

        // Calculate Ratio
        double yearToYearRatio = 0.0;
        if (lastYearBalance != 0) {
            yearToYearRatio = (annualBalance / lastYearBalance) * 100;
            // Round to 1 decimal place
            yearToYearRatio = Math.round(yearToYearRatio * 10.0) / 10.0;
        }

        // Add to Model
        model.addAttribute("annualBalance", annualBalance);
        model.addAttribute("yearToYear", yearToYearRatio);
        model.addAttribute("monthlyBalance", monthlyBalance);
        model.addAttribute("allTimeBalance", allTimeBalance);
    }
}