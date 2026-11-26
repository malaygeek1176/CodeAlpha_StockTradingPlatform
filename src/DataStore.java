import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * File I/O: saves and loads the user, portfolio and market state
 * from a simple pipe-separated text file.
 */
public class DataStore {

    public static void save(String path, User user, Market market) throws IOException {
        Portfolio p = user.getPortfolio();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write("USER|" + user.getName().replace("|", " ") + "|" + p.getInitialCash());
            w.newLine();
            w.write("CASH|" + p.getCash() + "|" + p.getRealizedProfit());
            w.newLine();
            w.write("DAY|" + market.getDay());
            w.newLine();
            for (Stock s : market.getAllStocks()) {
                w.write("PRICE|" + s.getSymbol() + "|" + s.getPrice() + "|" + s.getPreviousPrice());
                w.newLine();
            }
            for (Map.Entry<String, Integer> e : p.getHoldings().entrySet()) {
                w.write("HOLDING|" + e.getKey() + "|" + e.getValue() + "|" + p.getCostBasis().get(e.getKey()));
                w.newLine();
            }
            for (Transaction t : p.getTransactions()) {
                w.write("TX|" + t.toFileString());
                w.newLine();
            }
            for (Portfolio.Snapshot s : p.getHistory()) {
                w.write("SNAP|" + s.day + "|" + s.value);
                w.newLine();
            }
        }
    }

    /** Returns the saved user, or null if there is no save file. Market prices are restored too. */
    public static User load(String path, Market market) {
        File file = new File(path);
        if (!file.exists()) return null;

        String name = null;
        Portfolio portfolio = null;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                switch (parts[0]) {
                    case "USER":
                        name = parts[1];
                        portfolio = new Portfolio(Double.parseDouble(parts[2]));
                        break;
                    case "CASH":
                        portfolio.restore(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                        break;
                    case "DAY":
                        market.setDay(Integer.parseInt(parts[1]));
                        break;
                    case "PRICE":
                        Stock s = market.getStock(parts[1]);
                        if (s != null) s.restorePrices(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
                        break;
                    case "HOLDING":
                        portfolio.getHoldings().put(parts[1], Integer.parseInt(parts[2]));
                        portfolio.getCostBasis().put(parts[1], Double.parseDouble(parts[3]));
                        break;
                    case "TX":
                        portfolio.getTransactions().add(Transaction.fromFileParts(
                                java.util.Arrays.copyOfRange(parts, 1, parts.length)));
                        break;
                    case "SNAP":
                        portfolio.addSnapshot(Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Warning: could not read save file (" + e.getMessage() + "). Starting fresh.");
            return null;
        }
        return (name == null || portfolio == null) ? null : new User(name, portfolio);
    }
}
