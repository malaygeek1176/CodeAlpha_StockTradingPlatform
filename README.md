# CodeAlpha_StockTradingPlatform

**CodeAlpha Java Internship - Task 2: Stock Trading Platform**

A console-based stock trading simulator written in Java using Object-Oriented Programming.
Buy and sell stocks with virtual money, watch prices change day by day, and track how your
portfolio performs over time. Your data is saved to a file so you can continue later.

## Features
- **Market data display** - 8 stocks with price, change and % change
- **Buy / Sell operations** - with validation (insufficient funds, not enough shares, invalid quantity)
- **Portfolio view** - holdings, average cost, current value, profit/loss per stock, cash balance
- **Performance tracking over time** - value recorded each market day, shown with a text bar chart
- **Transaction history** - every buy/sell with date, day, quantity and price
- **Market simulation** - "advance to next day" moves prices randomly (max +/- 8%)
- **File I/O persistence** - portfolio, market prices and history saved to `portfolio_data.txt`
- Starts with **$10,000** virtual cash

## OOP design
| Class | Responsibility |
|---|---|
| `Stock` | One stock: symbol, name, current & previous price |
| `Market` | Holds all stocks, simulates daily price movement, shows market data |
| `Transaction` | One BUY/SELL record |
| `Portfolio` | Cash, holdings, cost basis, transactions, performance history, buy/sell logic |
| `User` | A trader who owns a portfolio |
| `TradingException` | Custom exception for invalid orders |
| `DataStore` | Saves / loads everything using file I/O |
| `Main` | Console menu |

## How to run
Requires **JDK 8 or newer**. Run from the project root folder:

```bash
javac -d out src/*.java
java -cp out Main
```

## Menu
```
 1. View market data
 2. Buy stock
 3. Sell stock
 4. View my portfolio
 5. View portfolio performance over time
 6. View transaction history
 7. Advance market to next day
 8. Save data
 0. Save & exit
```

## Sample output
```
======================== MY PORTFOLIO ========================
SYMBOL   QTY   AVG COST      PRICE        VALUE          P/L
--------------------------------------------------------------
AAPL       6     185.50     188.28      1129.68       +16.68
TSLA       5     240.60     219.62      1098.10      -104.90
--------------------------------------------------------------
Cash balance      : $    7,684.00
Total value       : $    9,911.78
Total profit/loss : -$88.22 (-0.88%)
```

## Author
Built as part of the CodeAlpha Java Programming Internship.
