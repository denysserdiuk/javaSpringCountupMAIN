package denysserdiuk.controller;

import denysserdiuk.dto.BudgetDisplayDto;
import denysserdiuk.dto.BudgetSubmissionDto;
import denysserdiuk.model.Budget;
import denysserdiuk.model.Users;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetLinesService;
import denysserdiuk.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BudgetRestController {

    private final BudgetLinesService budgetLinesService;
    private final UserRepository userRepository;

    @Autowired
    public BudgetRestController(BudgetLinesService budgetLinesService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.budgetLinesService = budgetLinesService;
    }

    @GetMapping("/userBalance")
    public Map<String, Double> getUserMonthlyBalance() {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);
        return budgetLinesService.getMonthlyBalance(user.getId());
    }

    @GetMapping("/CurrentMonthLossesByCategory")
    public Map<String, Double> getCurrentMonthLossesByCategory() {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);
        return budgetLinesService.getCurrentMonthLossCategoryPercentages(user);
    }

    // SECURED: Returns DTOs instead of Entities
    @GetMapping("/GetCustomMonthYearBudget")
    public ResponseEntity<List<BudgetDisplayDto>> customBudgets(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        List<Budget> budgetLines = budgetLinesService.getCurrentMonthBudgetLines(user, month, year);

        // Convert to DTOs
        List<BudgetDisplayDto> dtos = new ArrayList<>();
        for(Budget b : budgetLines) {
            BudgetDisplayDto dto = new BudgetDisplayDto();
            dto.setId(b.getId());
            dto.setDescription(b.getDescription());
            dto.setAmount(b.getAmount());
            dto.setCategory(b.getCategory());
            dto.setType(b.getType());
            dto.setDate(b.getDate());
            dtos.add(dto);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // SECURED: Accepts DTO
    @PostMapping("/AddBudgetItem")
    public ResponseEntity<String> addBudgetLineExpanse(@RequestBody BudgetSubmissionDto budgetDto) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setDescription(budgetDto.getDescription());
        budget.setAmount(budgetDto.getAmount());

        // Handle "New Category" logic if applicable
        if ("Other".equals(budgetDto.getCategory()) && budgetDto.getNewCategory() != null) {
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

        return ResponseEntity.ok("Budget item saved successfully");
    }

    @PostMapping("/updateBudgetItem")
    public ResponseEntity<String> updateBudgetItem(@RequestParam Map<String, String> params) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        try {
            Long id = Long.parseLong(params.get("id"));
            Optional<Budget> budgetOptional = budgetLinesService.findByIdAndUser(id, user);

            if (budgetOptional.isPresent()) {
                Budget budget = budgetOptional.get();
                // Only update fields that are present in params
                if(params.containsKey("description")) budget.setDescription(params.get("description"));
                if(params.containsKey("amount")) budget.setAmount(Double.parseDouble(params.get("amount")));
                if(params.containsKey("date")) budget.setDate(LocalDate.parse(params.get("date")));

                budgetLinesService.saveBudgetLine(budget);
                return new ResponseEntity<>("Budget item updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Budget item not found or unauthorized", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid data format", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/deleteBudgetItem/{id}")
    public ResponseEntity<String> deleteBudgetItem(@PathVariable("id") Long id) {
        String username = SecurityUtils.getAuthenticatedUsername();
        Users user = userRepository.findByUsername(username);

        Optional<Budget> budgetOptional = budgetLinesService.findByIdAndUser(id, user);

        if (budgetOptional.isPresent()) {
            budgetLinesService.deleteBudgetLine(budgetOptional.get());
            return ResponseEntity.ok("Budget item deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget item not found or unauthorized");
        }
    }
}