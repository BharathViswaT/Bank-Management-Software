import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Date;

// Base class for all types of bank accounts
abstract class BankAccount {
    protected String accountNo;
    protected double balance;
    protected String customerName;

    public BankAccount(String accountNo, String customerName, double balance) {
        this.accountNo = accountNo;
        this.customerName = customerName;
        this.balance = balance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited Rs." + amount + " into account " + accountNo);
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn Rs." + amount + " from account " + accountNo);
        } else {
            System.out.println("Insufficient balance in account " + accountNo);
        }
    }

    public double getBalance() {
        return balance;
    }

    // Abstract method to be implemented by subclasses
    public abstract void displayAccountInfo();
}

// SavingsAccount class
class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(String accountNo, String customerName, double balance, double interestRate) {
        super(accountNo, customerName, balance);
        this.interestRate = interestRate;
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("Savings Account - " + customerName + " | Account No: " + accountNo + " | Balance: Rs." + balance + " | Interest Rate: " + interestRate + "%");
    }
}

// BankManagementSystem class to handle all accounts and database connection
class BankManagementSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/data"; // Database name
    private static final String USER = "root"; // MySQL username
    private static final String PASS = "12345"; // MySQL password

    private ArrayList<BankAccount> accounts = new ArrayList<>();

    public static Connection connectToDatabase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Successfully connected to the database!");
        } catch (SQLException e) {
            System.out.println("Connection to database failed!");
            e.printStackTrace();
        }
        return connection;
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
        try (Connection connection = connectToDatabase()) {
            String query = "INSERT INTO bank_accounts (accountNo, customerName, balance) VALUES ('"
                    + account.accountNo + "', '" + account.customerName + "', " + account.getBalance() + ")";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Account added to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayAccountsFromDB() {
        try (Connection connection = connectToDatabase()) {
            String query = "SELECT * FROM bank_accounts";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String accountNo = rs.getString("accountNo");
                String customerName = rs.getString("customerName");
                double balance = rs.getDouble("balance");
                System.out.println("Account No: " + accountNo + ", Customer: " + customerName + ", Balance: Rs." + balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayReports(String accountNo) {
        if (accountNo == null || accountNo.isEmpty()) {
            System.out.println("===== Bank Reports =====");
            System.out.println("Total Accounts: " + accounts.size());
            double totalBalance = 0;
            for (BankAccount account : accounts) {
                totalBalance += account.getBalance();
            }
            System.out.println("Total Balance in the Bank: Rs." + totalBalance);
        } else {
            boolean accountFound = false;
            for (BankAccount account : accounts) {
                if (account.accountNo.equals(accountNo)) {
                    System.out.println("Account Details:");
                    account.displayAccountInfo();
                    accountFound = true;
                    break;
                }
            }
            if (!accountFound) {
                System.out.println("No account found with account number: " + accountNo);
            }
        }
    }
}

// Main class
public class BankManagementSoftware {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankManagementSystem bankSystem = new BankManagementSystem();

        while (true) {
            System.out.println("\n==== Bank Management System ====");
            System.out.println("1. Create Savings Account");
            System.out.println("2. Display Accounts from Database");
            System.out.println("3. Display Reports");
            System.out.println("4. Loan and Credit Management");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Account Number: ");
                    String accountNo = scanner.next();
                    System.out.print("Enter Customer Name: ");
                    String customerName = scanner.next();
                    System.out.print("Enter Initial Balance: ");
                    double balance = scanner.nextDouble();
                    System.out.print("Enter Interest Rate: ");
                    double interestRate = scanner.nextDouble();
                    SavingsAccount savingsAccount = new SavingsAccount(accountNo, customerName, balance, interestRate);
                    bankSystem.addAccount(savingsAccount);
                    System.out.println("Savings Account created successfully.");
                    break;

                case 2:
                    bankSystem.displayAccountsFromDB();
                    break;

                case 3:
                    System.out.print("Do you want to view reports for a specific account? (y/n): ");
                    char viewSpecific = scanner.next().toLowerCase().charAt(0);
                    if (viewSpecific == 'y') {
                        System.out.print("Enter Account Number: ");
                        String reportAccountNo = scanner.next();
                        bankSystem.displayReports(reportAccountNo);
                    } else {
                        bankSystem.displayReports(null);
                    }
                    break;

                case 4:
                    System.out.print("Do you want to (1) Get loan (2) view loan details (3) to update loan details? ");
                    int option = scanner.nextInt();
                    if (option == 1) {
                        System.out.print("Enter Customer ID: ");
                        int customerId = scanner.nextInt();

                        System.out.print("Enter Loan Amount: ");
                        double loanAmount = scanner.nextDouble();

                        System.out.print("Enter Interest Rate (%): ");
                        int interest = scanner.nextInt();

                        System.out.print("Enter Loan Term (in months): ");
                        int loanTerm = scanner.nextInt();

                        System.out.print("Enter Start Date (yyyy-mm-dd): ");
                        String startDateString = scanner.next();

                        Date startDate = Date.valueOf(startDateString);
                        Loan.addLoan(customerId, loanAmount, interest, loanTerm, startDate);
                    } else if (option == 2) {
                        System.out.print("Enter your loan_ID: ");
                        int id = scanner.nextInt();
                        Loan.getLoanDetails(id);

                    } else if (option == 3) {
                        System.out.print("Enter your loan_ID: ");
                        int id = scanner.nextInt();
                        System.out.print("Enter the status of Loan (Active/Inactive): ");
                        scanner.nextLine();
                        String status = scanner.nextLine();
                        Loan.updateLoanStatus(id, status);
                    }
                    break;

                case 5:
                    System.out.println("Exiting the system...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
