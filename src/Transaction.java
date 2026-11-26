import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A record of one BUY or SELL operation.
 */
public class Transaction {
    public enum Type { BUY, SELL }

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final int day;
    private final LocalDateTime time;

    public Transaction(Type type, String symbol, int quantity, double price, int day) {
        this(type, symbol, quantity, price, day, LocalDateTime.now());
    }

    public Transaction(Type type, String symbol, int quantity, double price, int day, LocalDateTime time) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.day = day;
        this.time = time;
    }

    public Type getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public int getDay() { return day; }
    public double getTotal() { return quantity * price; }

    /** One line used when saving to file. */
    public String toFileString() {
        return type + "|" + symbol + "|" + quantity + "|" + price + "|" + day + "|" + time.format(FORMAT);
    }

    public static Transaction fromFileParts(String[] p) {
        return new Transaction(Type.valueOf(p[0]), p[1], Integer.parseInt(p[2]),
                Double.parseDouble(p[3]), Integer.parseInt(p[4]),
                LocalDateTime.parse(p[5], FORMAT));
    }

    @Override
    public String toString() {
        return String.format("%s | Day %-3d | %-4s | %-6s | %4d x $%8.2f = $%10.2f",
                time.format(FORMAT), day, type, symbol, quantity, price, getTotal());
    }
}
