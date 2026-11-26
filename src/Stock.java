/**
 * Represents a single tradable stock in the market.
 */
public class Stock {
    private final String symbol;
    private final String name;
    private double price;
    private double previousPrice;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.previousPrice = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getPreviousPrice() { return previousPrice; }

    /** Moves the current price into "previous" and sets a new current price. */
    public void updatePrice(double newPrice) {
        this.previousPrice = this.price;
        this.price = Math.round(newPrice * 100.0) / 100.0;
    }

    /** Used when loading saved data so both prices are restored exactly. */
    public void restorePrices(double price, double previousPrice) {
        this.price = price;
        this.previousPrice = previousPrice;
    }

    public double getChange() {
        return price - previousPrice;
    }

    public double getChangePercent() {
        return previousPrice == 0 ? 0 : (getChange() / previousPrice) * 100.0;
    }
}
