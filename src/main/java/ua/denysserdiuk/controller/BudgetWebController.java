package denysserdiuk.controller;

import denysserdiuk.dto.BudgetDisplayDto;
import denysserdiuk.dto.BudgetSubmissionDto;
import denysserdiuk.model.Budget;
import denysserdiuk.model.Users;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetLinesService;
import denysserdiuk.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BudgetWebController {
    private final BudgetLinesService budgetLinesService;
    private final UserRepository userRepository;

    public BudgetWebController(BudgetLinesService budgetLinesService, UserRepository userRepository) {
        this.budgetLinesService = budgetLinesService;
        this.userRepository = userRepository;
    }

    // SECURED: Accepts DTO from Form
    @PostMapping("/AddBudgetItem")
    public String addBudgetLineExpanse(BudgetSubmissionDto budgetDto, Model model) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        // Map DTO to Entity
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setDescription(budgetDto.getDescription());
        budget.setAmount(budgetDto.getAmount());

        // Category Logic: If "Other" is selected, use the text input
        if ("Other".equalsIgnoreCase(budgetDto.getCategory()) &&
                budgetDto.getNewCategory() != null && !budgetDto.getNewCategory().isEmpty()) {
            budget.setCategory(budgetDto.getNewCategory());
        } else {
            budget.setCategory(budgetDto.getCategory());
        }

        budget.setType(budgetDto.getType());

        if (budgetDto.getDate() == null) {
            budget.setDate(LocalDate.now());
        } else {
            budget.setDate(budgetDto.getDate());
        }

        budgetLinesService.addBudgetLine(budget);
        model.addAttribute("message", "Budget Line added!");

        return "redirect:/home";
    }

    // SECURED: Returns DTOs for the Dashboard Table
    @GetMapping("/currentMonthBudgets")
    @ResponseBody
    public List<BudgetDisplayDto> getCurrentMonthBudgets() {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        List<Budget> entities = budgetLinesService.getCurrentMonthBudgetLines(
                user,
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear()
        );

        // Convert to DTOs
        List<BudgetDisplayDto> dtos = new ArrayList<>();
        for (Budget b : entities) {
            BudgetDisplayDto dto = new BudgetDisplayDto();
            dto.setId(b.getId());
            dto.setDescription(b.getDescription());
            dto.setAmount(b.getAmount());
            dto.setCategory(b.getCategory());
            dto.setType(b.getType());
            dto.setDate(b.getDate());
            dtos.add(dto);
        }

        return dtos;
    }
}