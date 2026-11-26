import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A user's portfolio: cash, share holdings, transaction history and
 * a record of total value over time (for performance tracking).
 */
public class Portfolio {

    /** Portfolio value recorded at the end of a market day. */
    public static class Snapshot {
        public final int day;
        public final double value;
        public Snapshot(int day, double value) {
            this.day = day;
            this.value = value;
        }
    }

    private double cash;
    private final double initialCash;
    private double realizedProfit = 0;
    private final Map<String, Integer> holdings = new LinkedHashMap<>();
    private final Map<String, Double> costBasis = new LinkedHashMap<>();   // total money spent on shares still held
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Snapshot> history = new ArrayList<>();

    public Portfolio(double initialCash) {
        this.initialCash = initialCash;
        this.cash = initialCash;
    }

    // ---------------------------------------------------------------- trading

    public void buy(Stock stock, int quantity, int day) throws TradingException {
        if (quantity <= 0) {
            throw new TradingException("Quantity must be greater than zero.");
        }
        double cost = stock.getPrice() * quantity;
        if (cost > cash) {
            throw new TradingException(String.format(
                    "Insufficient funds. Order costs $%.2f but you only have $%.2f.", cost, cash));
        }
        cash -= cost;
        holdings.merge(stock.getSymbol(), quantity, Integer::sum);
        costBasis.merge(stock.getSymbol(), cost, Double::sum);
        transactions.add(new Transaction(Transaction.Type.BUY, stock.getSymbol(), quantity, stock.getPrice(), day));
    }

    public void sell(Stock stock, int quantity, int day) throws TradingException {
        if (quantity <= 0) {
            throw new TradingException("Quantity must be greater than zero.");
        }
        int owned = getQuantity(stock.getSymbol());
        if (owned < quantity) {
            throw new TradingException("You only own " + owned + " share(s) of " + stock.getSymbol() + ".");
        }
        double avgCost = costBasis.get(stock.getSymbol()) / owned;
        double proceeds = stock.getPrice() * quantity;

        realizedProfit += proceeds - (avgCost * quantity);
        cash += proceeds;

        int remaining = owned - quantity;
        if (remaining == 0) {
            holdings.remove(stock.getSymbol());
            costBasis.remove(stock.getSymbol());
        } else {
            holdings.put(stock.getSymbol(), remaining);
            costBasis.put(stock.getSymbol(), avgCost * remaining);
        }
        transactions.add(new Transaction(Transaction.Type.SELL, stock.getSymbol(), quantity, stock.getPrice(), day));
    }

    // ------------------------------------------------------------- valuation

    public int getQuantity(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }

    public double getHoldingsValue(Market market) {
        double total = 0;
        for (Map.Entry<String, Integer> e : holdings.entrySet()) {
            total += market.getStock(e.getKey()).getPrice() * e.getValue();
        }
        return total;
    }

    public double getTotalValue(Market market) {
        return cash + getHoldingsValue(market);
    }

    public double getProfitLoss(Market market) {
        return getTotalValue(market) - initialCash;
    }

    // ----------------------------------------------------------- performance

    /** Records (or updates) the portfolio value for the given market day. */
    public void recordSnapshot(int day, Market market) {
        double value = getTotalValue(market);
        if (!history.isEmpty() && history.get(history.size() - 1).day == day) {
            history.set(history.size() - 1, new Snapshot(day, value));
        } else {
            history.add(new Snapshot(day, value));
        }
    }

    public void addSnapshot(int day, double value) {      // used when loading from file
        history.add(new Snapshot(day, value));
    }

    // ---------------------------------------------------------------- display

    public void displayPortfolio(Market market) {
        System.out.println();
        System.out.println("======================== MY PORTFOLIO ========================");
        if (holdings.isEmpty()) {
            System.out.println("You do not own any stocks yet.");
        } else {
            System.out.printf("%-7s %5s %10s %10s %12s %12s%n",
                    "SYMBOL", "QTY", "AVG COST", "PRICE", "VALUE", "P/L");
            System.out.println("--------------------------------------------------------------");
            for (Map.Entry<String, Integer> e : holdings.entrySet()) {
                String sym = e.getKey();
                int qty = e.getValue();
                double avg = costBasis.get(sym) / qty;
                double price = market.getStock(sym).getPrice();
                System.out.printf("%-7s %5d %10.2f %10.2f %12.2f %+12.2f%n",
                        sym, qty, avg, price, price * qty, (price - avg) * qty);
            }
        }
        System.out.println("--------------------------------------------------------------");
        System.out.printf("Cash balance      : $%,12.2f%n", cash);
        System.out.printf("Holdings value    : $%,12.2f%n", getHoldingsValue(market));
        System.out.printf("Total value       : $%,12.2f%n", getTotalValue(market));
        System.out.printf("Starting capital  : $%,12.2f%n", initialCash);
        System.out.printf("Total profit/loss : %s$%,.2f (%+.2f%%)%n",
                getProfitLoss(market) >= 0 ? "+" : "-", Math.abs(getProfitLoss(market)),
                (getProfitLoss(market) / initialCash) * 100);
        System.out.printf("Realized profit   : %s$%,.2f%n", realizedProfit >= 0 ? "+" : "-", Math.abs(realizedProfit));
        System.out.println("==============================================================");
    }

    /** Shows portfolio value for each recorded day with a simple text bar chart. */
    public void displayPerformance() {
        System.out.println();
        System.out.println("================= PORTFOLIO PERFORMANCE OVER TIME =================");
        if (history.isEmpty()) {
            System.out.println("No history yet. Advance the market to record performance.");
            return;
        }
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (Snapshot s : history) {
            min = Math.min(min, s.value);
            max = Math.max(max, s.value);
        }
        double range = Math.max(max - min, 1.0);
        System.out.printf("%-5s %14s %10s  %s%n", "DAY", "VALUE($)", "VS START", "GRAPH");
        for (Snapshot s : history) {
            int bars = 5 + (int) (((s.value - min) / range) * 30);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bars; i++) sb.append('#');
            System.out.printf("%-5d %,14.2f %+9.2f%%  %s%n",
                    s.day, s.value, ((s.value - initialCash) / initialCash) * 100, sb);
        }
        System.out.println("(Bars are scaled between your lowest and highest recorded value.)");
        System.out.println("===================================================================");
    }

    public void displayTransactions() {
        System.out.println();
        System.out.println("============================ TRANSACTION HISTORY ============================");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : transactions) System.out.println(t);
        }
        System.out.println("=============================================================================");
    }

    // ------------------------------------------------------ getters for saving

    public double getCash() { return cash; }
    public double getInitialCash() { return initialCash; }
    public double getRealizedProfit() { return realizedProfit; }
    public Map<String, Integer> getHoldings() { return holdings; }
    public Map<String, Double> getCostBasis() { return costBasis; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<Snapshot> getHistory() { return history; }

    /** Restores state from a saved file. */
    public void restore(double cash, double realizedProfit) {
        this.cash = cash;
        this.realizedProfit = realizedProfit;
    }
}
