package denysserdiuk.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class BudgetSubmissionDto {
    private String description;
    private Double amount;
    private String category;
    private String type; // "profit" or "loss"

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // Optional: New category field if "Other" was selected
    private String newCategory;

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNewCategory() { return newCategory; }
    public void setNewCategory(String newCategory) { this.newCategory = newCategory; }
}