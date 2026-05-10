import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account with credentials, balance, and transaction history.
 */
public class Account {

    private final String userId;
    private final String pin;
    private final String holderName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String userId, String pin, String holderName, double initialBalance) {
        this.userId      = userId;
        this.pin         = pin;
        this.holderName  = holderName;
        this.balance     = initialBalance;
    }

    // --- Getters ---

    public String getUserId()     { return userId; }
    public String getHolderName() { return holderName; }
    public double getBalance()    { return balance; }

    /** Returns true if the provided PIN matches this account's PIN. */
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    // --- Balance operations ---

    public void credit(double amount) {
        balance += amount;
    }

    public void debit(double amount) {
        balance -= amount;
    }

    // --- Transaction history ---

    public void addTransaction(String type, double amount) {
        transactions.add(new Transaction(type, amount, balance));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
