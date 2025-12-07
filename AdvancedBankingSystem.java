
import java.io.*;
import java.util.*;

class BankAccount implements Serializable {
    private String accountNumber;
    private String holderName;
    private int pin;
    private double balance;
    private ArrayList<String> transactions;

    public BankAccount(String accNo, String name, int pin) {
        this.accountNumber = accNo;
        this.holderName = name;
        this.pin = pin;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        transactions.add("Account created with balance 0");
    }

    public boolean validatePin(int inputPin) {
        return this.pin == inputPin;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add("Deposited: " + amount);
            System.out.println("✅ Amount Deposited Successfully.");
        } else {
            System.out.println("❌ Invalid Amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add("Withdrawn: " + amount);
            System.out.println("✅ Amount Withdrawn Successfully.");
        } else {
            System.out.println("❌ Insufficient Balance.");
        }
    }

    public double getBalance() {
        return balance;
    }

    public void displayDetails() {
        System.out.println("\n--- Account Details ---");
        System.out.println("Account No: " + accountNumber);
        System.out.println("Name: " + holderName);
        System.out.println("Balance: " + balance);
    }

    public void showTransactions() {
        System.out.println("\n--- Transaction History ---");
        for (String t : transactions) {
            System.out.println(t);
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

class BankManager {
    private HashMap<String, BankAccount> accounts;
    private final String FILE_NAME = "bankdata.dat";

    public BankManager() {
        loadData();
    }

    public void createAccount(String accNo, String name, int pin) {
        if (accounts.containsKey(accNo)) {
            System.out.println("❌ Account already exists.");
            return;
        }

        BankAccount acc = new BankAccount(accNo, name, pin);
        accounts.put(accNo, acc);
        saveData();
        System.out.println("✅ Account Created Successfully.");
    }

    public BankAccount login(String accNo, int pin) {
        BankAccount acc = accounts.get(accNo);
        if (acc != null && acc.validatePin(pin)) {
            return acc;
        }
        return null;
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
        } catch (Exception e) {
            System.out.println("Error Saving Data");
        }
    }

    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (HashMap<String, BankAccount>) ois.readObject();
        } catch (Exception e) {
            accounts = new HashMap<>();
        }
    }

    public void update() {
        saveData();
    }
}

public class AdvancedBankingSystem {
    static Scanner sc = new Scanner(System.in);
    static BankManager manager = new BankManager();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== BANKING SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Thank You for using Banking System!");
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    static void createAccount() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.next();
        System.out.print("Enter Name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.print("Set PIN: ");
        int pin = sc.nextInt();

        manager.createAccount(accNo, name, pin);
    }

    static void login() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.next();
        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        BankAccount acc = manager.login(accNo, pin);

        if (acc == null) {
            System.out.println("❌ Invalid Login.");
            return;
        }

        System.out.println("✅ Login Successful.");
        userMenu(acc);
    }

    static void userMenu(BankAccount acc) {
        while (true) {
            System.out.println("\n1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Transaction History");
            System.out.println("5. Account Details");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            int ch = sc.nextInt();

            switch (ch) {
                case 1:
                    System.out.print("Enter Amount: ");
                    acc.deposit(sc.nextDouble());
                    manager.update();
                    break;
                case 2:
                    System.out.print("Enter Amount: ");
                    acc.withdraw(sc.nextDouble());
                    manager.update();
                    break;
                case 3:
                    System.out.println("Current Balance: " + acc.getBalance());
                    break;
                case 4:
                    acc.showTransactions();
                    break;
                case 5:
                    acc.displayDetails();
                    break;
                case 6:
                    System.out.println("Logged Out Successfully.");
                    return;
                default:
                    System.out.println("Invalid Option!");
            }
        }
    }
}
