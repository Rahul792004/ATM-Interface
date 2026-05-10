import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all ATM operations: deposit, withdraw, and transfer.
 * Validates inputs and updates account state accordingly.
 */
public class ATMService {

    private static final Logger LOGGER = Logger.getLogger(ATMService.class.getName());

    /** Strips control characters from any string before logging. */
    private static String sanitize(String value) {
        return value == null ? "" : value.replaceAll("[\\r\\n\\t\\x00-\\x1F\\x7F]", "_");
    }

    /** Deposits a positive amount into the account. */
    public boolean deposit(Account account, double amount) {
        if (amount <= 0) {
            LOGGER.warning("  [!] Deposit amount must be greater than zero.");
            return false;
        }
        account.credit(amount);
        account.addTransaction("DEPOSIT", amount);
        LOGGER.log(Level.INFO, "  [✓] Deposited successfully. New balance: {0}",
                String.format("$%.2f", account.getBalance()));
        return true;
    }

    /** Withdraws amount from account if sufficient balance exists. */
    public boolean withdraw(Account account, double amount) {
        if (amount <= 0) {
            LOGGER.warning("  [!] Withdrawal amount must be greater than zero.");
            return false;
        }
        if (amount > account.getBalance()) {
            LOGGER.log(Level.WARNING, "  [!] Insufficient balance. Available: {0}",
                    String.format("$%.2f", account.getBalance()));
            return false;
        }
        account.debit(amount);
        account.addTransaction("WITHDRAWAL", amount);
        LOGGER.log(Level.INFO, "  [✓] Withdrawn successfully. New balance: {0}",
                String.format("$%.2f", account.getBalance()));
        return true;
    }

    /**
     * Transfers amount from sender to receiver if sufficient balance exists.
     * Returns false if receiver account is not found or balance is insufficient.
     */
    public boolean transfer(Account sender, Account receiver, double amount) {
        if (receiver == null) {
            LOGGER.warning("  [!] Recipient account not found.");
            return false;
        }
        if (amount <= 0) {
            LOGGER.warning("  [!] Transfer amount must be greater than zero.");
            return false;
        }
        if (amount > sender.getBalance()) {
            LOGGER.log(Level.WARNING, "  [!] Insufficient balance. Available: {0}",
                    String.format("$%.2f", sender.getBalance()));
            return false;
        }
        sender.debit(amount);
        sender.addTransaction("TRANSFER TO " + receiver.getUserId(), amount);
        receiver.credit(amount);
        receiver.addTransaction("TRANSFER FROM " + sender.getUserId(), amount);

        LOGGER.log(Level.INFO, "  [✓] Transferred to {0}. New balance: {1}",
                new Object[]{sanitize(receiver.getUserId()),
                             String.format("$%.2f", sender.getBalance())});
        return true;
    }
}
