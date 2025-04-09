package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.service.AccountService;
import br.com.compass.bankchallenge.service.ClientService;
import br.com.compass.bankchallenge.service.UserService;

public class App {
    
    public static void main(String[] args) {	
    	
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        
        System.out.println("Application closed");
        
    }
    

    public static void mainMenu(Scanner scanner) {
        
    	boolean running = true;

        while (running) {
            System.out.println("========= Main Menu =========");
            System.out.println("|| 1. Login                ||");
            System.out.println("|| 2. Account Opening      ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
  //              	loginSection(scanner);
                    return;
                case 2:
                    accountOpeningSection(scanner);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public static void bankMenu(Scanner scanner) {
        
    	boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // ToDo...
                    System.out.println("Deposit.");
                    break;
                case 2:
                    // ToDo...
                    System.out.println("Withdraw.");
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 0:
                    // ToDo...
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    
/*    public static void loginSection(Scanner scanner) {

    	AuthService authService = new AuthService();

    	System.out.println("\n=== Login ===");
        System.out.print("Enter your login (email): ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User loggedUser = authService.login(email, password);

        if (loggedUser != null) {
            System.out.println("Login successful! Welcome, " + loggedUser.getName());
        	bankMenu(scanner);
        } else {
            System.out.println("Login failed! Check email/password.");
        }
        
        authService.close();
    } */
    
    public static void accountOpeningSection(Scanner scanner) { 
    	
        System.out.println("\n=== Account Opening ===");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        UserService userService = new UserService();
        
        if (userService.getUserByEmail(email) == null) {

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter a password: ");
            String password = scanner.nextLine();
            
            System.out.print("Enter your CPF: ");
            String cpf = scanner.nextLine();
            
            System.out.print("Enter your phone number: ");
            String phone = scanner.nextLine();
            
            LocalDate birthDate = null;

            while (birthDate == null) {
                System.out.print("Enter your date of birth (dd/MM/yyyy): ");
                String birthDateStr = scanner.nextLine();
                try {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    birthDate = LocalDate.parse(birthDateStr, dtf);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format! Please try again.");
                }
            }
            
            System.out.println("Select account type:");
            System.out.println("1. Checking");
            System.out.println("2. Savings");
            System.out.println("3. Payroll");
            
            int accountTypeChoice = scanner.nextInt();
            scanner.nextLine(); 
            
            AccountType accountType;
            if (accountTypeChoice == 1) {
                accountType = AccountType.CHECKING;
            } else if (accountTypeChoice == 2) {
                accountType = AccountType.SAVINGS;
            } else if (accountTypeChoice == 3) {
                accountType = AccountType.PAYROLL;
            } else {
                System.out.println("Invalid account type selected. Defaulting to checking.");
                accountType = AccountType.CHECKING;
            }
            
            ClientService clientService = new ClientService();
            try {
                clientService.registerClient(name, email, password, cpf, phone, birthDate);
                
                Client client = (Client) userService.getUserByEmail(email);
                Account newAccount = new Account(client, accountType);
                client.addAccount(newAccount);
                
                AccountService accountService = new AccountService();
                accountService.registerAccount(client, accountType);
                
                System.out.println("Account creation successful! Your client registration is complete.");
                
            } catch (IllegalArgumentException e) {
                System.out.println("Error creating account: " + e.getMessage());
            }
            
        } 
        
        else {

            System.out.println("There is already a user with that email.");
            System.out.print("Do you want to log in now to open a new linked account? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            
            if (answer.equals("y") || answer.equals("yes")) {
                System.out.println("Login section..."); // To do
            } else {
                System.out.println("Returning to the main menu...");
            }
        }
    }
        
}
