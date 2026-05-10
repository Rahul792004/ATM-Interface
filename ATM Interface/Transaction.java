import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a single ATM transaction record.
 */
public class Transaction {

    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final String timestamp;

    // Locale.ROOT ensures consistent formatting regardless of system locale
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    public Transaction(String type, double amount, double balanceAfter) {
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp    = LocalDateTime.now().format(FORMATTER);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "  [%s]  %-22s  Amount: $%8.2f  |  Balance: $%8.2f",
                timestamp, type, amount, balanceAfter);
    }
}
