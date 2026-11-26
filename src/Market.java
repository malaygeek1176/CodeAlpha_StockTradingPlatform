import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simulated stock market. Prices move randomly each time a new "day" starts.
 */
public class Market {
    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random random = new Random();
    private int day = 1;

    public Market() {
        addStock("AAPL", "Apple Inc.", 185.50);
        addStock("GOOGL", "Alphabet Inc.", 140.25);
        addStock("MSFT", "Microsoft Corp.", 375.80);
        addStock("AMZN", "Amazon.com Inc.", 155.10);
        addStock("TSLA", "Tesla Inc.", 240.60);
        addStock("NFLX", "Netflix Inc.", 480.30);
        addStock("INFY", "Infosys Ltd.", 18.45);
        addStock("TCS", "Tata Consultancy", 42.90);
    }

    private void addStock(String symbol, String name, double price) {
        stocks.put(symbol, new Stock(symbol, name, price));
    }

    public Stock getStock(String symbol) {
        return symbol == null ? null : stocks.get(symbol.trim().toUpperCase());
    }

    public Collection<Stock> getAllStocks() { return stocks.values(); }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    /** Advances the market by one day and randomly moves every stock price (max +/- 8%). */
    public void nextDay() {
        for (Stock s : stocks.values()) {
            double changePct = random.nextGaussian() * 0.025;          // typical daily move ~2.5%
            changePct = Math.max(-0.08, Math.min(0.08, changePct));    // cap at +/- 8%
            double newPrice = Math.max(1.0, s.getPrice() * (1 + changePct));
            s.updatePrice(newPrice);
        }
        day++;
    }

    /** Prints the market data table. */
    public void display() {
        System.out.println();
        System.out.println("=============== MARKET DATA  (Day " + day + ") ===============");
        System.out.printf("%-7s %-20s %10s %9s %9s%n", "SYMBOL", "COMPANY", "PRICE($)", "CHANGE", "CHANGE%");
        System.out.println("-----------------------------------------------------------");
        for (Stock s : stocks.values()) {
            String arrow = s.getChange() > 0 ? "^" : (s.getChange() < 0 ? "v" : "-");
            System.out.printf("%-7s %-20s %10.2f %+9.2f %+8.2f%% %s%n",
                    s.getSymbol(), s.getName(), s.getPrice(),
                    s.getChange(), s.getChangePercent(), arrow);
        }
        System.out.println("===========================================================");
    }
}
