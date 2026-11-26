/**
 * A trader using the platform. Each user owns one portfolio.
 */
public class User {
    private final String name;
    private final Portfolio portfolio;

    public User(String name, Portfolio portfolio) {
        this.name = name;
        this.portfolio = portfolio;
    }

    public String getName() { return name; }
    public Portfolio getPortfolio() { return portfolio; }
}
