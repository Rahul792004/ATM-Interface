import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Main entry point for the ATM console application.
 * Handles user login, menu navigation, and delegates operations to ATMService.
 */
public class ATMApp {

    private static final Map<String, Account> accounts = new HashMap<>();
    private static final ATMService atmService = new ATMService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(ATMApp.class.getName());

    public static void main(String[] args) {
        setupLogger();
        seedAccounts();

        LOGGER.info("╔══════════════════════════════════╗");
        LOGGER.info("║       Welcome to Java ATM        ║");
        LOGGER.info("╚══════════════════════════════════╝");

        Account loggedInAccount = login();
        if (loggedInAccount != null) {
            showMenu(loggedInAccount);
        }

        LOGGER.info("  Thank you for using Java ATM. Goodbye!");
        scanner.close();
    }

    // ── Logger setup: plain output without Java log prefix ───────────────────

    private static void setupLogger() {
        Logger root = Logger.getLogger("");
        for (var h : root.getHandlers()) {
            root.removeHandler(h);
        }
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord r) {
                return r.getMessage() + System.lineSeparator();
            }
        });
        root.addHandler(handler);
    }

    /** Strips control characters from any string before logging. */
    private static String sanitize(String value) {
        return value == null ? "" : value.replaceAll("[\\r\\n\\t\\x00-\\x1F\\x7F]", "_");
    }

    // ── Seed demo accounts ────────────────────────────────────────────────────

    private static void seedAccounts() {
        accounts.put("user01", new Account("user01", "1234", "Alice Johnson", 5000.00));
        accounts.put("user02", new Account("user02", "5678", "Bob Smith",     3200.00));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    private static Account login() {
        final int MAX_ATTEMPTS = 3;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("\n  Enter User ID : ");
            String userId = scanner.nextLine().trim();

            System.out.print("  Enter PIN     : ");
            String pin = scanner.nextLine().trim();

            Account account = accounts.get(userId);
            if (account != null && account.validatePin(pin)) {
                LOGGER.log(Level.INFO, "  [✓] Login successful. Welcome, {0}!",
                        sanitize(account.getHolderName()));
                return account;
            }

            LOGGER.log(Level.WARNING, "  [!] Invalid credentials for user: {0} | Attempt {1}/{2}",
                    new Object[]{sanitize(userId), attempt, MAX_ATTEMPTS});
        }

        LOGGER.severe("  [✗] Too many failed attempts. Card blocked.");
        return null;
    }

    // ── Main menu ─────────────────────────────────────────────────────────────

    private static void showMenu(Account account) {
        boolean running = true;

        while (running) {
            printMenu(account);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showTransactionHistory(account);
                    break;
                case "2":
                    handleWithdraw(account);
                    break;
                case "3":
                    handleDeposit(account);
                    break;
                case "4":
                    handleTransfer(account);
                    break;
                case "5":
                    LOGGER.info("  [✓] Logged out successfully.");
                    running = false;
                    break;
                default:
                    LOGGER.warning("  [!] Invalid option. Please choose 1-5.");
                    break;
            }
        }
    }

    private static void printMenu(Account account) {
        LOGGER.info("┌──────────────────────────────────┐");
        LOGGER.log(Level.INFO, "│  Account: {0}│",
                String.format("%-23s", sanitize(account.getUserId())));
        LOGGER.log(Level.INFO, "│  Balance: {0}│",
                String.format("$%-22.2f", account.getBalance()));
        LOGGER.info("├──────────────────────────────────┤");
        LOGGER.info("│  1. Transaction History          │");
        LOGGER.info("│  2. Withdraw                     │");
        LOGGER.info("│  3. Deposit                      │");
        LOGGER.info("│  4. Transfer                     │");
        LOGGER.info("│  5. Logout                       │");
        LOGGER.info("└──────────────────────────────────┘");
        System.out.print("  Choose an option: ");
    }

    // ── Operation handlers ────────────────────────────────────────────────────

    private static void showTransactionHistory(Account account) {
        List<Transaction> history = account.getTransactions();
        LOGGER.info("  ── Transaction History ──────────────────────────────────────────");
        if (history.isEmpty()) {
            LOGGER.info("  No transactions yet.");
        } else {
            history.forEach(t -> LOGGER.log(Level.INFO, "{0}", t.toString()));
        }
        LOGGER.info("  ─────────────────────────────────────────────────────────────────");
    }

    private static void handleWithdraw(Account account) {
        System.out.print("\n  Enter withdrawal amount: $");
        double amount = readAmount();
        if (amount >= 0) {
            atmService.withdraw(account, amount);
        }
    }

    private static void handleDeposit(Account account) {
        System.out.print("\n  Enter deposit amount: $");
        double amount = readAmount();
        if (amount >= 0) {
            atmService.deposit(account, amount);
        }
    }

    private static void handleTransfer(Account account) {
        System.out.print("\n  Enter recipient User ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(account.getUserId())) {
            LOGGER.warning("  [!] Cannot transfer to your own account.");
            return;
        }

        Account recipient = accounts.get(recipientId);
        if (recipient == null) {
            LOGGER.log(Level.WARNING, "  [!] Recipient not found: {0}", sanitize(recipientId));
            return;
        }

        System.out.print("  Enter transfer amount: $");
        double amount = readAmount();
        if (amount >= 0) {
            atmService.transfer(account, recipient, amount);
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /** Reads a non-negative double from console; returns -1 on invalid input. */
    private static double readAmount() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            LOGGER.warning("  [!] Invalid amount. Please enter a numeric value.");
            return -1;
        }
    }
}
