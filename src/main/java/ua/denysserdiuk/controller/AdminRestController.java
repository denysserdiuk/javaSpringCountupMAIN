package denysserdiuk.controller;

import denysserdiuk.dto.AdminUserActionDto;
import denysserdiuk.model.Budget;
import denysserdiuk.model.Shares;
import denysserdiuk.model.Users;
import denysserdiuk.repository.BudgetRepository;
import denysserdiuk.repository.SharesRepository;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetLinesService;
import denysserdiuk.service.SharesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final SharesRepository sharesRepository;
    private final BudgetLinesService budgetLinesService;
    private final SharesService sharesService;

    public AdminRestController(UserRepository userRepository, BudgetRepository budgetRepository, SharesRepository sharesRepository, BudgetLinesService budgetLinesService, SharesService sharesService) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.sharesRepository = sharesRepository;
        this.budgetLinesService = budgetLinesService;
        this.sharesService = sharesService;
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody AdminUserActionDto request) {
        String email = request.getEmail(); // Changed from getUsername()

        // Use findByEmail instead of findByUsername
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            // 1. Delete Budgets
            List<Budget> budgetLines = budgetRepository.findByUserId(user.getId());
            for (Budget budget: budgetLines) {
                budgetLinesService.deleteBudgetLine(budget);
            }

            // 2. Delete Shares
            List<Shares> shares = sharesRepository.findByUserId(user.getId());
            for (Shares share: shares){
                sharesService.deleteShare(share);
            }

            // 3. Delete User
            userRepository.delete(user);

            return new ResponseEntity<>("User with email '" + email + "' deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
    }
}