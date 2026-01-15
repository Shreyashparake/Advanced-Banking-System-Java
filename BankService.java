import java.sql.*;
import java.util.Scanner;

public class BankService {
    Scanner sc = new Scanner(System.in);

    public void register() {
        try {
            System.out.print("Enter Username: ");
            String user = sc.next();
            System.out.print("Enter Password: ");
            String pass = sc.next();

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username,password) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, user);
            ps.setString(2, pass);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int userId = rs.getInt(1);

            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO accounts(user_id,balance) VALUES(?,0)"
            );
            ps2.setInt(1, userId);
            ps2.executeUpdate();

            System.out.println("Account created successfully!");

        } catch (Exception e) {
            System.out.println("Username already exists.");
        }
    }

    public void login() {
        try {
            System.out.print("Username: ");
            String user = sc.next();
            System.out.print("Password: ");
            String pass = sc.next();

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT user_id FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(1);
                dashboard(userId);
            } else {
                System.out.println("Invalid Login!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dashboard(int userId) throws Exception {
        while (true) {
            System.out.println("\n1. Deposit\n2. Withdraw\n3. Balance\n4. Transactions\n5. Logout");
            int ch = sc.nextInt();

            if (ch == 1) deposit(userId);
            else if (ch == 2) withdraw(userId);
            else if (ch == 3) balance(userId);
            else if (ch == 4) transactions(userId);
            else break;
        }
    }

    void deposit(int userId) throws Exception {
        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "UPDATE accounts SET balance=balance+? WHERE user_id=?"
        );
        ps.setDouble(1, amt);
        ps.setInt(2, userId);
        ps.executeUpdate();

        addTransaction(userId, "Deposit", amt);
        System.out.println("Deposited successfully");
    }

    void withdraw(int userId) throws Exception {
        System.out.print("Amount: ");
        double amt = sc.nextDouble();

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT balance FROM accounts WHERE user_id=?"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        double bal = rs.getDouble(1);

        if (bal >= amt) {
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE accounts SET balance=balance-? WHERE user_id=?"
            );
            ps2.setDouble(1, amt);
            ps2.setInt(2, userId);
            ps2.executeUpdate();

            addTransaction(userId, "Withdraw", amt);
            System.out.println("Withdraw Successful");
        } else {
            System.out.println("Insufficient Balance");
        }
    }

    void balance(int userId) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT balance FROM accounts WHERE user_id=?"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        System.out.println("Current Balance: ₹" + rs.getDouble(1));
    }

    void transactions(int userId) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT account_no FROM accounts WHERE user_id=?"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int acc = rs.getInt(1);

        PreparedStatement ps2 = con.prepareStatement(
            "SELECT * FROM transactions WHERE account_no=?"
        );
        ps2.setInt(1, acc);
        ResultSet t = ps2.executeQuery();

        while (t.next()) {
            System.out.println(t.getString("type") + " | " +
                               t.getDouble("amount") + " | " +
                               t.getTimestamp("txn_date"));
        }
    }

    void addTransaction(int userId, String type, double amt) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT account_no FROM accounts WHERE user_id=?"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        rs.next();

        PreparedStatement ps2 = con.prepareStatement(
            "INSERT INTO transactions(account_no,type,amount) VALUES(?,?,?)"
        );
        ps2.setInt(1, rs.getInt(1));
        ps2.setString(2, type);
        ps2.setDouble(3, amt);
        ps2.executeUpdate();
    }
}
