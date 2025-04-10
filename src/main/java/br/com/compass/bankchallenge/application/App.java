package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import br.com.compass.bankchallenge.config.DatabaseInitializer;
import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.domain.enums.RefundStatus;
import br.com.compass.bankchallenge.repository.RefundRequestRepository;
import br.com.compass.bankchallenge.service.AccountService;
import br.com.compass.bankchallenge.service.AuthService;
import br.com.compass.bankchallenge.service.ClientService;
import br.com.compass.bankchallenge.service.OperationService;
import br.com.compass.bankchallenge.service.RefundRequestService;
import br.com.compass.bankchallenge.service.UserService;
import br.com.compass.bankchallenge.util.JPAUtil;

public class App {
    
    public static void main(String[] args) {	
    	
        DatabaseInitializer.loadInitialManager();
    	
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        
        System.out.println("Application closed");
        JPAUtil.shutdown();

    }
    
    
// Public
    
    public static void mainMenu(Scanner scanner) {	// Feito
        
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
	                User user = loginSection(scanner);
	                if (user != null) {
	                    if (user.getAccessLevel() == AccessLevel.MANAGER)
	                        managementMenu(scanner, (Manager) user);
	                    else
	                        bankMenu(scanner, (Client) user);
	                }
                	break;
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
    
    public static User loginSection(Scanner scanner) {
        AuthService authService = new AuthService();

        try {
            System.out.println("\n=== Login ===");
            System.out.print("Enter your login (email): ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User loggedUser = authService.login(email, password);

            if (loggedUser != null) {
                System.out.println("Login successful! Welcome, " + loggedUser.getName());
                return loggedUser;
            } else {
                System.out.println("Login failed! Check email/password.");
                return null;
            }

        } catch (Exception e) {
            System.out.println("An unexpected error occurred during login.");
            e.printStackTrace();
            return null;
        }
    }

// Management
    
    public static void managementMenu(Scanner scanner, Manager manager) { // Falta -- Lembrar de gerente master
    	
    	boolean running = true;

        while (running) {
            System.out.println("========= Management Menu =========");
	        System.out.println("|| 1. Pending refund requests    ||");
            System.out.println("|| 0. Exit                       ||");
            System.out.println("===================================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                	managerRefundRequestSection(scanner, manager);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    
    public static void managerRefundRequestSection(Scanner scanner, Manager manager) {
        RefundRequestService refundRequestService = new RefundRequestService();
        RefundRequestRepository refundRequestRepository = new RefundRequestRepository();

        var pendingRequests = refundRequestRepository.findByStatus(RefundStatus.PENDING);

        if (pendingRequests == null || pendingRequests.isEmpty()) {
            System.out.println("There are no pending refund requests.");
            return;
        }

        System.out.println("\n=== Pending Refund Requests ===");
        for (var request : pendingRequests) {
            System.out.printf("ID: %d | Operation ID: %d | Client ID: %d | Requested on: %s%n",
                    request.getId(),
                    request.getOperation().getId(),
                    request.getClient().getId(),
                    request.getRequestDate());
        }

        System.out.print("Enter the ID of the refund request to process (or 0 to cancel): ");
        Long requestId = scanner.nextLong();
        scanner.nextLine();

        if (requestId == 0) return;

        System.out.print("Approve this refund? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();

        try {
            if (input.equals("y") || input.equals("yes")) {
                refundRequestService.approveRefund(manager, requestId);
                System.out.println("Refund approved successfully.");
            } else if (input.equals("n") || input.equals("no")) {
                refundRequestService.rejectRefund(manager, requestId);
                System.out.println("Refund rejected successfully.");
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Error processing refund: " + e.getMessage());
        }
    }
    
// Client
    
    public static void bankMenu(Scanner scanner, Client client) {	// Falta
        
    	boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 6. Refund request list  ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                	depositSection(scanner);
                    break;
                case 2:
                	withdrawSection(scanner);
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                	transferSection(scanner);
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 6:
                	clientRefundRequestSection(scanner, client.getId());
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
    
    public static void accountOpeningSection(Scanner scanner) { // Feito
    	
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
            
            if (accountTypeChoice == 1) 
                accountType = AccountType.CHECKING;
            
            else if (accountTypeChoice == 2)
                accountType = AccountType.SAVINGS;
            
            else if (accountTypeChoice == 3)
                accountType = AccountType.PAYROLL;
            
            else {
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
            
            if (answer.equals("y") || answer.equals("yes"))
            	loginSection(scanner);
            
            else
                System.out.println("Returning to the main menu...");
            
        }
    }
    
    public static void depositSection(Scanner scanner) { // Feito
    	
        System.out.println("Deposit.");
       
        try {

            scanner.nextLine();
        	
            System.out.print("Enter account id: ");
            Long accountId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter deposit amount: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());

            OperationService operationService = new OperationService();
            operationService.deposit(accountId, amount);
            
            System.out.println("Deposit successful!");
        } catch (Exception e) {
            System.out.println("Error during deposit: " + e.getMessage());
        }
        
    }
    
    public static void withdrawSection(Scanner scanner) { // Feito
    	
    	System.out.println("\n=== Withdraw ===");
        try {
        	
            scanner.nextLine();

            System.out.print("Enter account id: ");
            Long accountId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter withdrawal amount: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            
            OperationService operationService = new OperationService();
            operationService.withdrawal(accountId, amount);
            
            System.out.println("Withdrawal successful!");
        } catch (Exception e) {
            System.out.println("Error during withdrawal: " + e.getMessage());
        }
    }

    public static void transferSection(Scanner scanner) { // Feito -- Lembrar de lógica de salvar como depósito na conta source.
    	
    	System.out.println("\n=== Transfer ===");
        try {

            scanner.nextLine();

            System.out.print("Enter source account id: ");
            Long sourceAccountId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter destination account id: ");
            Long destinationAccountId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter transfer amount: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            
            OperationService operationService = new OperationService();
            operationService.transfer(sourceAccountId, destinationAccountId, amount);
            
            System.out.println("Transfer successful!");
        } catch (Exception e) {
            System.out.println("Error during transfer: " + e.getMessage());
        }

    }
    
    public static void clientRefundRequestSection(Scanner scanner, Long clientId) {
        RefundRequestService refundRequestService = new RefundRequestService();

        System.out.println("\n=== Refund Request Menu ===");
        System.out.println("1. Request a refund");
        System.out.println("2. View your refund requests");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                System.out.print("Enter the operation ID for the refund: ");
                Long operationId = scanner.nextLong();
                scanner.nextLine();

                try {
                    refundRequestService.requestRefund(operationId, clientId);
                    System.out.println("Refund request submitted successfully.");
                } catch (Exception e) {
                    System.out.println("Failed to request refund: " + e.getMessage());
                }
                break;

            case 2:
                var requests = new RefundRequestRepository().findByClientId(clientId);

                if (requests == null || requests.isEmpty()) {
                    System.out.println("You have no refund requests.");
                } else {
                    System.out.println("\n=== Your Refund Requests ===");
                    for (var req : requests) {
                        System.out.printf("ID: %d | Operation ID: %d | Status: %s | Requested on: %s%n",
                                req.getId(),
                                req.getOperation().getId(),
                                req.getStatus(),
                                req.getRequestDate());
                    }
                }
                break;

            default:
                System.out.println("Invalid option.");
        }
    }
    

    
}
