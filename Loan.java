
    import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Loan {

    private static final String URL = "jdbc:mysql://localhost:3306/data";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    // Connect to the database
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Add a new loan
    public static void addLoan(int customerId, double loanAmount, double interestRate, int loanTerm, Date startDate) {
        String query = "INSERT INTO bank_management (customer_id, loan_amount, interest_rate, loan_term, start_date, status) VALUES (?, ?, ?, ?, ?, 'Active')";
        int loanId = -1;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setDouble(2, loanAmount);
            pstmt.setDouble(3, interestRate);
            pstmt.setInt(4, loanTerm);
            pstmt.setDate(5, startDate);
            
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                loanId = rs.getInt(1); 
                System.out.println("Loan added successfully. Loan ID: " + loanId);
            }
            System.out.println("Loan added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve loan details by loan ID
    public static void getLoanDetails(int loanId) {
        String query = "SELECT * FROM bank_management WHERE loan_id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Loan ID: " + rs.getInt("loan_id"));
                System.out.println("Customer ID: " + rs.getInt("customer_id"));
                System.out.println("Loan Amount: $" + rs.getDouble("loan_amount"));
                System.out.println("Interest Rate: " + rs.getDouble("interest_rate") + "%");
                System.out.println("Loan Term: " + rs.getInt("loan_term") + " months");
                System.out.println("Start Date: " + rs.getDate("start_date"));
                System.out.println("Status: " + rs.getString("status"));
            } else {
                System.out.println("Loan not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateLoanStatus(int loanId, String status) {
        String query = "UPDATE bank_management SET status = ? WHERE loan_id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Loan status updated successfully.");
            } else {
                System.out.println("Loan not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}


