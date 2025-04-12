package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.compass.bankchallenge.config.DatabaseInitializer;
import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.domain.Operation;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.domain.enums.RefundStatus;
import br.com.compass.bankchallenge.repository.AccountRepository;
import br.com.compass.bankchallenge.repository.RefundRequestRepository;
import br.com.compass.bankchallenge.service.AccountService;
import br.com.compass.bankchallenge.service.AuthService;
import br.com.compass.bankchallenge.service.ClientService;
import br.com.compass.bankchallenge.service.OperationService;
import br.com.compass.bankchallenge.service.RefundRequestService;
import br.com.compass.bankchallenge.service.StatementService;
import br.com.compass.bankchallenge.service.UserService;
import br.com.compass.bankchallenge.util.JPAUtil;

public class App {
    	
    public static void main(String[] args) {	
    	
        DatabaseInitializer.loadInitialManager();
    	
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        
        JPAUtil.shutdown();

    }
    
    
// Public ##########################################################################################   
    
    public static void mainMenu(Scanner scanner) {	
        
    	boolean running = true;

        while (running) {

            System.out.println("\n\n\n\n========= Main Menu =========");
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
            	    if (user != null) 
            	    	handleLogin(user, scanner);
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
    
    public static User loginSection(Scanner scanner) { // Done
    	
        AuthService authService = new AuthService();

        try {
            System.out.println("\n\n\n\n========= Login =========");
            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User loggedUser = authService.login(email, password);
            
            if (loggedUser != null) {
                System.out.println("\n\n\n\nLogin successful! Welcome, " + loggedUser.getName());
                return loggedUser;
            }             	
            else {
                System.out.println("\n\n\n\nLogin failed!");
                return null;
            }

        } catch (Exception e) {
            System.out.println("\n\n\n\nAn unexpected error occurred during login.");
            e.printStackTrace();
            return null;
        }
    }
    
    public static void accountOpeningSection(Scanner scanner) { // Done
    	
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

                System.out.println("\n\n\n\nAccount creation successful! Your client registration is complete.");
                
            } catch (IllegalArgumentException e) {
                System.out.println("\n\n\n\nError creating account: " + e.getMessage());
            }
            
        } 
        
        else {

            System.out.println("\n\n\n\nThere is already a user with that email.");
            System.out.print("\n\nDo you want to log in now to open a new linked account? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            
            if (answer.equals("y") || answer.equals("yes"))
            	loginSection(scanner);
            
            else
                System.out.println("\n\n\n\nReturning to the main menu...");
            
        }
    }
    
    public static void handleLogin(User user, Scanner scanner) { // Done
        
    	if (user.getAccessLevel() == AccessLevel.MANAGER) {
            managementMenu(scanner, (Manager) user);
        } else if (user instanceof Client client) {
            List<Account> accounts = client.getAccounts();

            if (accounts.size() > 1) {
                System.out.println("\nYou have multiple accounts. Please select one:");

                for (int i = 0; i < accounts.size(); i++) {
                    Account acc = accounts.get(i);
                    System.out.printf("%d. %s - Account No: %s - Balance: %.2f%n",
                        i + 1, acc.getAccountType(), acc.getAccountNumber(), acc.getBalance());
                }

                int selectedIndex = -1;
                while (selectedIndex < 1 || selectedIndex > accounts.size()) {
                    System.out.print("Enter the number of the account you want to access: ");
                    if (scanner.hasNextInt()) {
                        selectedIndex = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        scanner.nextLine();
                        System.out.println("Invalid input. Please enter a number.");
                    }
                }

                Account selectedAccount = accounts.get(selectedIndex - 1);
                bankMenu(scanner, client, selectedAccount);

            } else if (accounts.size() == 1) {
                bankMenu(scanner, client, accounts.get(0));
            } else {
                System.out.println("You do not have any accounts linked to your profile.");
            }
        } else {
            System.out.println("Unknown user type.");
        }
    }
    
    public static Boolean exitSection(Scanner scanner) { // Done
    	
        System.out.println("\n\n\n\nExiting...");
        return false;
    	
    }
    
// Management ##########################################################################################
    
    public static void managementMenu(Scanner scanner, Manager manager) { // Done
    																	     	   	
    	boolean running = true;

        while (running) {
            System.out.println("\n\n\n\n========= Management Menu =========");
	        System.out.println("|| 1. Pending refund requests    ||");
	        System.out.println("|| 2. Locked accounts            ||");
	        
	        if(manager.getId() == 1)
	        	System.out.println("|| 3. Create manager             ||");
	                    	        
	        System.out.println("|| 0. Exit                       ||");
            System.out.println("===================================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                	managerRefundRequestSection(scanner, manager);
                    break;
                case 2:
                	lockedAccountsSection(scanner);
                    break;
                case 3:
                	if (manager.getId() == 1)
                        createManagerSection(scanner);
                    else
                        System.out.println("\n\n\n\nInvalid option! Please try again.");
                    break;
                case 0:
                	running = exitSection(scanner);
                    return;
                default:
                    System.out.println("\n\n\n\nInvalid option! Please try again.");
            }
        }
    }
    
    public static void managerRefundRequestSection(Scanner scanner, Manager manager) { // Done
        RefundRequestService refundRequestService = new RefundRequestService();
        RefundRequestRepository refundRequestRepository = new RefundRequestRepository();

        var pendingRequests = refundRequestRepository.findByStatus(RefundStatus.PENDING);

        if (pendingRequests == null || pendingRequests.isEmpty()) {
            System.out.println("\n\n\n\nThere are no pending refund requests.");
            return;
        }

        System.out.println("\n\n\n\n=== Pending Refund Requests ===");
        for (var request : pendingRequests) {
            System.out.printf("ID: %d | Operation ID: %d | Client ID: %d | Requested on: %s%n",
                    request.getId(),
                    request.getOperation().getId(),
                    request.getClient().getId(),
                    request.getRequestDate());
        }

        System.out.print("\n\nEnter the ID of the refund request to process (or 0 to cancel): ");
        Long requestId = scanner.nextLong();
        scanner.nextLine();

        if (requestId == 0) return;

        System.out.print("\nApprove this refund? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();

        try {
            if (input.equals("y") || input.equals("yes")) {
                refundRequestService.approveRefund(manager, requestId);
                System.out.println("\n\nRefund approved successfully.");
            } else if (input.equals("n") || input.equals("no")) {
                refundRequestService.rejectRefund(manager, requestId);
                System.out.println("\n\nRefund rejected successfully.");
            } else {
                System.out.println("\n\nInvalid choice.");
            }
        } catch (Exception e) {
            System.out.println("\n\nError processing refund: " + e.getMessage());
        }
    }
    
    public static void lockedAccountsSection(Scanner scanner) { // Done
    	UserService userService = new UserService();
        List<User> blockedUsers = userService.findBlockedUsers();

        if (blockedUsers == null || blockedUsers.isEmpty()) {
            System.out.println("\n\nThere are no locked users.");
            return;
        }

        System.out.println("\n\n\n\n=== Locked Users ===");
        for (User user : blockedUsers) {
            System.out.printf("ID: %d | Name: %s | Email: %s%n", user.getId(), user.getName(), user.getEmail());
        }

        System.out.print("\n\nEnter the ID of the user to unlock (or 0 to cancel): ");
        Long userId = scanner.nextLong();
        scanner.nextLine();

        if (userId == 0) return;

        User userToUnlock = userService.findById(userId);

        if (userToUnlock == null || !userToUnlock.isBlocked()) {
            System.out.println("\n\nInvalid ID or user is not locked.");
            return;
        }

        System.out.print("\n\nDo you want to unlock this user? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            userToUnlock.setBlocked(false);
            userToUnlock.setFailedLoginAttempts(0);
            userService.update(userToUnlock);
            System.out.println("\n\nUser unlocked successfully.");
        } else {
            System.out.println("\n\nOperation canceled.");
        }
    }
    
    public static void createManagerSection(Scanner scanner) { // Done
    	
    	scanner.nextLine();

        System.out.println("\n\n\n\n=== Create New Manager ===");

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            System.out.println("\n\nAll fields are required. Manager not created.");
            return;
        }

        AuthService authService = new AuthService();
        authService.registerManager(name, email, password);    	
    }
    
  
// Client ##########################################################################################
    
    public static void bankMenu(Scanner scanner, Client client, Account account) {	//
        

    	boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 6. Refund request list  ||");
            System.out.println("|| 7. Create a new account ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                	depositSection(scanner);
                    break;
                case 2:
                	withdrawSection(scanner, account);
                    break;
                case 3:
                	checkBalanceSection(scanner);
                    break;
                case 4:
                	transferSection(scanner, account);
                    break;
                case 5:
                	bankStatementSection(scanner, client);
                    break;
                case 6:
                	clientRefundRequestSection(scanner, client.getId());
                    break;
                case 7:
                	createNewAccount(scanner);
                    break;
                case 0:
                	running = exitSection(scanner);
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
        
    public static void depositSection(Scanner scanner) { // Done
    	
    	 System.out.println("\n\n\n\n=== Deposit ===");

    	    try {
    	        scanner.nextLine(); 

    	        System.out.print("Enter the account number: ");
    	        String accountNumber = scanner.nextLine().trim();

    	        AccountRepository accountRepository = new AccountRepository();
    	        Account account = accountRepository.findByAccountNumber(accountNumber);

    	        if (account == null) {
    	            System.out.println("\n\nNo account found with the provided account number.");
    	            return;
    	        }

    	        System.out.printf("\nAccount found:%n - Number: %s%n - Owner: %s%n",
    	                account.getAccountNumber(),
    	                account.getClient().getName());

    	        System.out.print("\n\nEnter deposit amount: ");
    	        Double amount = Double.parseDouble(scanner.nextLine().trim());

    	        if (amount <= 0) {
    	            System.out.println("Amount must be greater than zero.");
    	            return;
    	        }

    	        System.out.printf("\n\nYou are about to deposit %.2f into account %s (Owner: %s). Confirm? (yes/no): ",
    	                amount, account.getAccountNumber(), account.getClient().getName());

    	        String confirmation = scanner.nextLine().trim().toLowerCase();
    	        if (!confirmation.equals("yes") && !confirmation.equals("y")) {
    	            System.out.println("\n\nDeposit cancelled.");
    	            return;
    	        }

    	        OperationService operationService = new OperationService();
    	        operationService.deposit(account.getId(), amount);

    	        System.out.println("\n\nDeposit successful!");
    	    } catch (Exception e) {
    	        System.out.println("\n\nError during deposit: " + e.getMessage());
    	    }
        
    }
    
    public static void withdrawSection(Scanner scanner, Account account) { // Done
    	
    	System.out.println("\n\n\n\n=== Withdraw ===");
        try {
        	
            scanner.nextLine();
            
            System.out.print("Enter withdrawal amount: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            
            OperationService operationService = new OperationService();
            operationService.withdrawal(account.getId(), amount);
            
            System.out.println("\n\nWithdrawal successful!");
        } catch (Exception e) {
            System.out.println("\n\nError during withdrawal: " + e.getMessage());
        }
    }

    public static void transferSection(Scanner scanner, Account account) { // Done
    	
    	System.out.println("\n\n\n\n=== Transfer ===");
        try {

            scanner.nextLine();
            
            System.out.print("Enter destination account number: ");
	        String destinationAccountNumber = scanner.nextLine().trim();
            
            AccountRepository accountRepository = new AccountRepository();
            
            Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);
            
            if (destinationAccount == null) {
                System.out.println("\n\nDestination account not found.");
                return;
            }
            
            System.out.print("Enter transfer amount: ");
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if(amount > account.getBalance()) {
            	System.out.println("\n\nYou do not have enough balance to make this transfer.");
                return;
            }
            
            System.out.printf("You are about to transfer %.2f from account %s (Owner: %s) to account %s (Owner: %s). Confirm? (yes/no): ",
                    amount, 
                    account.getAccountNumber(), account.getClient().getName(),
                    destinationAccount.getAccountNumber(), destinationAccount.getClient().getName());
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            
            
            if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                System.out.println("\n\nTransfer cancelled.");
                return;
            }
            
            OperationService operationService = new OperationService();
            operationService.transfer(account.getId(), destinationAccount.getId(), amount);
            
            System.out.println("\n\nTransfer successful!");
        } catch (Exception e) {
            System.out.println("\n\nError during transfer: " + e.getMessage());
        }
    }
    
    public static void clientRefundRequestSection(Scanner scanner, Long clientId) { //
        
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
    
    public static void bankStatementSection(Scanner scanner, Client client) { //
        System.out.println("\n=== Bank Statement Menu ===");
        System.out.println("0. Exit");
        System.out.println("1. View general statement");
        System.out.println("2. View deposit statement");
        System.out.println("3. View withdraw statement");
        System.out.println("4. View transfer statement");
        System.out.print("Choose an option: ");
        
        int option = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter period start (e.g., 01/04/2025 00:00): ");
        LocalDateTime start;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            start = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use dd/MM/yyyy HH:mm");
            return;
        }

        System.out.print("Enter period end (e.g., 30/04/2025 23:59): ");
        LocalDateTime end;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            end = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use dd/MM/yyyy HH:mm");
            return;
        }

        StatementService service = new StatementService();
        List<Operation> operations = new ArrayList<>();

        switch (option) {
        	case 0:
        		exitSection(scanner);
        		return;
            case 1:
                operations = service.viewStatement(client.getAccounts().get(0).getId(), start, end);
                break;
            case 2:
                operations = service.viewDepositStatement(client.getAccounts().get(0).getId(), start, end);
                break;
            case 3:
                operations = service.viewWithdrawalStatement(client.getAccounts().get(0).getId(), start, end);
                break;
            case 4:
                operations = service.viewTransferStatement(client.getAccounts().get(0).getId(), start, end);
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        if (operations.isEmpty()) {
            System.out.println("No operations found for your account in the given period.");
            return;
        }

        System.out.println("\n=== Statement Result ===");
        for (Operation op : operations) {
            System.out.printf("ID: %d | Type: %s | Amount: %.2f | Date: %s%n",
                op.getId(), op.getOperationType(), op.getAmount(), op.getOperationDate());
        }

        System.out.print("\nDo you want to export this statement to CSV? (yes/no): ");
        String exportAnswer = scanner.nextLine().trim().toLowerCase();

        if (exportAnswer.equals("yes") || exportAnswer.equals("y")) {
            System.out.print("Enter file path for CSV export (e.g., /path/to/extract.csv): ");
            String filePath = scanner.nextLine().trim();

            boolean success = service.exportStatementToCSV(operations, filePath);
            
            if (success) {
                System.out.println("CSV exported successfully to: " + filePath);
            } else {
                System.out.println("Failed to export CSV.");
            }
        }
    }
    
    public static void createNewAccount(Scanner scanner) { //
    	
    }
    
    public static void checkBalanceSection(Scanner scanner) { //
    	
    }
    
}
