package denysserdiuk.utils;

public class CalculateBalanceUtil {
    public static double calculateBalanceUtil(Double profit, Double loss){
        double balance = (profit == null ? 0 : profit) - (loss == null ? 0 : loss);
        return Math.round(balance * 100.0) / 100.0;
    }
}
