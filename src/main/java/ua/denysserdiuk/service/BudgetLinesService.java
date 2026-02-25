package denysserdiuk.service;

import denysserdiuk.model.Budget;
import denysserdiuk.model.Users;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BudgetLinesService {
    String addBudgetLine(Budget budget);
    Budget saveBudgetLine(Budget budget);
    void deleteBudgetLine(Budget budget);
    List<Budget> getCurrentMonthBudgetLines(Users user, int month, int year);
    Map<String, Double> getMonthlyBalance(long id);
    Double getAnnualBalance(long userId, int year);
    Double getMonthlyBalance(long userId, int month, int year);
    Double getAllTimeBalance(long userId);
    List<String>getUserCategories(long userID);
    Map<String, Double> getCurrentMonthLossCategoryPercentages(Users user);
    String updateBudgetLinesForUser(long userID);
    Optional<Budget> findByIdAndUser(Long id, Users user);


}
