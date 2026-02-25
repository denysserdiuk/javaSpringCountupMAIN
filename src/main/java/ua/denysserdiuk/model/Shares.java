package denysserdiuk.model;

import jakarta.persistence.*;
import denysserdiuk.model.Users;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shares")
public class Shares {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
    private String ticker;
    private double amount;
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    private double price;
    private double profit;

    public Shares(){}

    public Shares(Users user,
                  String ticker,
                  double amount,
                  LocalDate purchaseDate,
                  double price,
                  double profit){
        this.user = user;
        this.ticker = ticker;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.profit = profit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}

