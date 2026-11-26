/**
 * Thrown when a buy/sell request is invalid (not enough cash, not enough shares, etc.).
 */
public class TradingException extends Exception {
    public TradingException(String message) {
        super(message);
    }
}
