import java.io.IOException;
import java.util.Scanner;

/**
 * Console-based Stock Trading Platform.
 * CodeAlpha Java Internship - Task 2.
 */
public class Main {
    private static final String DATA_FILE = "portfolio_data.txt";
    private static final double STARTING_CASH = 10000.00;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Market market = new Market();
        User user = DataStore.load(DATA_FILE, market);

        System.out.println("*************************************************");
        System.out.println("*        CODEALPHA STOCK TRADING PLATFORM       *");
        System.out.println("*************************************************");

        if (user == null) {
            String name = readLine("Enter your name: ");
            if (name.isEmpty()) name = "Trader";
            user = new User(name, new Portfolio(STARTING_CASH));
            user.getPortfolio().recordSnapshot(market.getDay(), market);
            System.out.printf("Welcome, %s! You start with $%,.2f in virtual cash.%n", name, STARTING_CASH);
        } else {
            System.out.println("Welcome back, " + user.getName() + "! Your saved portfolio was loaded.");
        }

        Portfolio portfolio = user.getPortfolio();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ");
            switch (choice) {
                case "1":
                    market.display();
                    break;
                case "2":
                    trade(true, market, portfolio);
                    break;
                case "3":
                    trade(false, market, portfolio);
                    break;
                case "4":
                    portfolio.displayPortfolio(market);
                    break;
                case "5":
                    portfolio.displayPerformance();
                    break;
                case "6":
                    portfolio.displayTransactions();
                    break;
                case "7":
                    market.nextDay();
                    portfolio.recordSnapshot(market.getDay(), market);
                    System.out.println("Market advanced to Day " + market.getDay() + ". Prices have changed!");
                    market.display();
                    break;
                case "8":
                    saveData(user, market);
                    break;
                case "0":
                    saveData(user, market);
                    System.out.println("Goodbye, " + user.getName() + "! Happy trading.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("-------------- MAIN MENU --------------");
        System.out.println(" 1. View market data");
        System.out.println(" 2. Buy stock");
        System.out.println(" 3. Sell stock");
        System.out.println(" 4. View my portfolio");
        System.out.println(" 5. View portfolio performance over time");
        System.out.println(" 6. View transaction history");
        System.out.println(" 7. Advance market to next day");
        System.out.println(" 8. Save data");
        System.out.println(" 0. Save & exit");
        System.out.println("---------------------------------------");
    }

    private static void trade(boolean isBuy, Market market, Portfolio portfolio) {
        market.display();
        String symbol = readLine("Enter stock symbol: ").toUpperCase();
        Stock stock = market.getStock(symbol);
        if (stock == null) {
            System.out.println("Stock '" + symbol + "' was not found.");
            return;
        }
        System.out.printf("%s is trading at $%.2f. You own %d share(s). Cash: $%,.2f%n",
                stock.getSymbol(), stock.getPrice(), portfolio.getQuantity(symbol), portfolio.getCash());

        int quantity;
        try {
            quantity = Integer.parseInt(readLine("Enter quantity: "));
        } catch (NumberFormatException e) {
            System.out.println("Please enter a whole number.");
            return;
        }

        try {
            if (isBuy) {
                portfolio.buy(stock, quantity, market.getDay());
                System.out.printf("BOUGHT %d x %s @ $%.2f = $%,.2f%n",
                        quantity, symbol, stock.getPrice(), quantity * stock.getPrice());
            } else {
                portfolio.sell(stock, quantity, market.getDay());
                System.out.printf("SOLD %d x %s @ $%.2f = $%,.2f%n",
                        quantity, symbol, stock.getPrice(), quantity * stock.getPrice());
            }
            portfolio.recordSnapshot(market.getDay(), market);
        } catch (TradingException e) {
            System.out.println("Order rejected: " + e.getMessage());
        }
    }

    private static void saveData(User user, Market market) {
        try {
            DataStore.save(DATA_FILE, user, market);
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {          // input closed (e.g. Ctrl+D) -> exit cleanly
            System.out.println();
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }
}
