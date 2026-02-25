package denysserdiuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import denysserdiuk.model.Budget;
import denysserdiuk.model.Users;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget findByDescriptionAndUserId(String description, Long userId);
    Optional<Budget> findByIdAndUser(Long id, Users user);
    List<Budget> findByUserId(Long id);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND MONTH(b.date) = :month AND YEAR(b.date) = :year")
    List<Budget> findBudgetsByUserAndMonthYear(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId")
    List<Budget> findAllBudgetsById(@Param("userId") Long userId);

    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.user.id = :userId AND b.type = :type AND MONTH(b.date) = :month AND YEAR(b.date) = :year")
    Double findTotalByUserAndTypeAndMonth(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.user.id = :userId AND b.type = :type AND YEAR(b.date) = :year")
    Double findTotalByUserAndTypeAndYear(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("year") int year
    );

    @Query("SELECT SUM(b.amount) FROM Budget b WHERE b.user.id = :userId AND b.type = :type")
    Double findTotalByUserAndType(
            @Param("userId") Long userId,
            @Param("type") String type
    );

    @Query("SELECT DISTINCT b.category FROM Budget b WHERE b.user.id = :userId")
    List<String> findCategoriesByUser(@Param("userId") Long userId);

}
