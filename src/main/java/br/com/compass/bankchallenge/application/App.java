package br.com.compass.bankchallenge.application;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import br.com.compass.bankchallenge.exceptions.BusinessLogicException;
import br.com.compass.bankchallenge.exceptions.DatabaseException;
import br.com.compass.bankchallenge.exceptions.ValidationException;
import br.com.compass.bankchallenge.repository.AccountRepository;
import br.com.compass.bankchallenge.repository.RefundRequestRepository;
import br.com.compass.bankchallenge.service.AccountService;
import br.com.compass.bankchallenge.service.AuthService;
import br.com.compass.bankchallenge.service.ClientService;
import br.com.compass.bankchallenge.service.OperationService;
import br.com.compass.bankchallenge.service.RefundRequestService;
import br.com.compass.bankchallenge.service.StatementService;
import br.com.compass.bankchallenge.service.UserService;
import br.com.compass.bankchallenge.util.InputValidatorUtil;
import br.com.compass.bankchallenge.util.JPAUtil;

public class App {
    	
    public static void main(String[] args) {	
    	
    	Locale.setDefault(Locale.US);
    	
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

            int option = 0;
            try {
                String input = scanner.nextLine().trim();
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option! Please enter a number.");
                continue; 
            }

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
                    running = exitSection(scanner);
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    
    public static User loginSection(Scanner scanner) {
    	
        AuthService authService = new AuthService();

        try {
            System.out.println("\n\n\n\n========= Login =========");
            System.out.print("Login (CPF for clients, email for managers)\nEnter your login: ");
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
    
    public static void accountOpeningSection(Scanner scanner) {

        System.out.println("\n=== Account Opening ===");
        System.out.print("Enter your CPF (12345678909): ");
        String cpf = scanner.nextLine().trim();

        if (!InputValidatorUtil.isValidCpf(cpf)) {
            System.out.println("\nInvalid CPF format!");
            return;
        }

        ClientService clientService = new ClientService();

        if (clientService.findByCpf(cpf) == null) {

            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            if (!InputValidatorUtil.isValidName(name)) {
            	System.out.println("Invalid name! Must contain at least 2 characters and only letters.");
            	return;
            }
           
            System.out.print("Enter a password: ");
            String password = scanner.nextLine();

            System.out.print("Enter your email (example@email.com): ");
            String email = scanner.nextLine().trim();

            if (!InputValidatorUtil.isValidEmail(email)) {
            	System.out.println("\n\nInvalid email!\n");
            	return;
            }            

            System.out.print("Enter your phone number (XX)XXXX-XXXX: ");
            String phone = scanner.nextLine().trim();

            if (!InputValidatorUtil.isValidPhone(phone)) {
            	System.out.println("\n\nInvalid phone number! The format must be (XX)XXXX-XXXX or similar.\n");
            	return;
            }
            
            LocalDate birthDate = null;
            String birthDateStr = null;
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while (birthDate == null) {
                
            	System.out.print("Enter your date of birth (dd/MM/yyyy): ");
                birthDateStr = scanner.nextLine().trim();
                
                try {

                	if (!InputValidatorUtil.isValidDate(birthDateStr)) {
                    	System.out.println("Invalid date of birth!");
                    	return;
                	}
                                            
                    birthDate = LocalDate.parse(birthDateStr, dtf);
                    
                } catch (DateTimeParseException | ValidationException e) {
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

            try {

                clientService.registerClient(name, email, password, cpf, phone, birthDate);

                Client client = (Client) clientService.findByCpf(cpf);

                Account newAccount = new Account(client, accountType);
                client.addAccount(newAccount);

                AccountService accountService = new AccountService();
                accountService.registerAccount(client, accountType);

                System.out.println("\n\n\n\nAccount creation successful! Your client registration is complete.");

            } catch (ValidationException | IllegalArgumentException e) {
                System.out.println("\n\n\n\nError creating account: " + e.getMessage());
            } catch (BusinessLogicException | DatabaseException e) {
                System.out.println("\n\n\n\nCritical error occurred: " + e.getMessage());
            }

        } else {

            System.out.println("\n\n\n\nThere is already a client with that CPF.");
            System.out.println("\n\n\n\nReturning to the main menu...");

        }
    }
    
    public static void handleLogin(User user, Scanner scanner) {
        
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
    
    public static Boolean exitSection(Scanner scanner) {
    	
        System.out.println("\n\n\n\nExiting...");
        return false;
    	
    }
    
// Management ##########################################################################################
    
    public static void managementMenu(Scanner scanner, Manager manager) {
    																	     	   	
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
            
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("\n\n\n\nInvalid input! Please enter a number.");
                continue;
            }
            
            int option = scanner.nextInt();
            scanner.nextLine(); 

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
    
    public static void managerRefundRequestSection(Scanner scanner, Manager manager) { 
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

        Long requestId = null;
        while (true) {
            System.out.print("\n\nEnter the ID of the refund request to process (or 0 to cancel): ");
            if (scanner.hasNextLong()) {
                requestId = scanner.nextLong();
                scanner.nextLine();
                break;
            } else {
                scanner.nextLine(); 
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }

        if (requestId == 0)
            return;

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
            String errorMessage = e.getMessage().toLowerCase();

            if (errorMessage.contains("insufficient balance")) {
                System.out.println("\n\nRefund cannot be processed because the source account does not have sufficient balance.");
            } else if (errorMessage.contains("constraint") || errorMessage.contains("duplicate")) {
                System.out.println("\n\nThis refund has already been requested and cannot be duplicated.");
            } else {
                System.out.println("\n\nAn unexpected error occurred while processing the refund: " + e.getMessage());
            }
        }
    }
    
    public static void lockedAccountsSection(Scanner scanner) { 
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

    	    Long userId = null;
    	    while (true) {
    	        System.out.print("\n\nEnter the ID of the user to unlock (or 0 to cancel): ");
    	        if (scanner.hasNextLong()) {
    	            userId = scanner.nextLong();
    	            scanner.nextLine(); 
    	            break;
    	        } else {
    	            scanner.nextLine(); 
    	            System.out.println("Invalid input. Please enter a valid numeric ID.");
    	        }
    	    }

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
    
    public static void createManagerSection(Scanner scanner) { 
    	
	    System.out.println("\n\n\n\n=== Create New Manager ===");

	    System.out.print("Name: ");
	    String name = scanner.nextLine().trim();

	    System.out.print("Email (example@email.com): ");
	    String email = scanner.nextLine().trim();

	    System.out.print("Password: ");
	    String password = scanner.nextLine().trim();

	    if (name.isBlank() || email.isBlank() || password.isBlank()) {
	        System.out.println("\n\nAll fields are required. Manager not created.");
	        return;
	    }

	    if (!InputValidatorUtil.isValidName(name)) {
	        System.out.println("\n\nInvalid name format. Manager not created.");
	        return;
	    }

	    if (!InputValidatorUtil.isValidEmail(email)) {
	        System.out.println("\n\nInvalid email format. Manager not created.");
	        return;
	    }   	    

	    AuthService authService = new AuthService();
	    authService.registerManager(name, email, password);
	    System.out.println("\n\nManager created successfully."); 	
	    
    }
    
  
// Client ##########################################################################################
    
    public static void bankMenu(Scanner scanner, Client client, Account account) {	
       
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

            int option = -1;
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("\nInvalid input! Please enter a number.\n");
                scanner.nextLine();
                continue;
            }

            switch (option) {
                case 1:
                	depositSection(scanner, account);
                    break;
                case 2:
                	withdrawSection(scanner, account);
                    break;
                case 3:
                	checkBalanceSection(scanner, account);
                    break;
                case 4:
                	transferSection(scanner, account);
                    break;
                case 5:
                	bankStatementSection(scanner, client, account);
                    break;
                case 6:
                	clientRefundRequestSection(scanner, account, client);
                    break;
                case 7:
                	createNewAccount(scanner, client);
                    break;
                case 0:
                	running = exitSection(scanner);
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
        
    public static void depositSection(Scanner scanner, Account account) {
    	
    	System.out.println("\n\n\n\n=== Deposit ===");
        
        System.out.print("Enter Deposit amount: ");
        String input = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("\n\nInvalid amount. Deposit must be greater than zero.");
                return;
            }

            account = refreshAccount(account);

            OperationService operationService = new OperationService();
            operationService.deposit(account.getId(), amount);

            System.out.println("\n\nDeposit successful!");
            
        } catch (NumberFormatException e) {
            System.out.println("\n\nInvalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("\n\nError during deposit: " + e.getMessage());
        }
    }
    
    public static void withdrawSection(Scanner scanner, Account account) { 
        
    	System.out.println("\n\n\n\n=== Withdraw ===");

        System.out.print("Enter withdrawal amount: ");
        String input = scanner.nextLine().trim();

        try {
            double amount = Double.parseDouble(input);

            if (amount <= 0) {
                System.out.println("\n\nInvalid amount. Withdrawal must be greater than zero.");
                return;
            }

            account = refreshAccount(account);

            OperationService operationService = new OperationService();
            operationService.withdrawal(account.getId(), amount);

            System.out.println("\n\nWithdrawal successful!");
        } catch (NumberFormatException e) {
            System.out.println("\n\nInvalid input. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("\n\nError during withdrawal: " + e.getMessage());
        }
    }

    public static void transferSection(Scanner scanner, Account account) { 

        System.out.println("\n\n\n\n=== Transfer ===");

        try {

            System.out.print("Enter destination account number: ");
            String destinationAccountNumber = scanner.nextLine().trim();

            AccountRepository accountRepository = new AccountRepository();
            Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);

            if (destinationAccount == null) {
                System.out.println("\n\nDestination account not found.");
                return;
            }

            System.out.print("Enter transfer amount: ");
            String amountInput = scanner.nextLine().trim();
            double amount;

            try {
                amount = Double.parseDouble(amountInput);
            } catch (NumberFormatException e) {
                System.out.println("\n\nInvalid input. Please enter a valid number for the transfer amount.");
                return;
            }

            if (amount <= 0) {
                System.out.println("\n\nTransfer amount must be greater than zero.");
                return;
            }

            account = refreshAccount(account);

            if (amount > account.getBalance()) {
                System.out.println("\n\nYou do not have enough balance to make this transfer.");
                return;
            }

            System.out.printf(
                "You are about to transfer %.2f from account %s (Owner: %s) to account %s (Owner: %s). Confirm? (yes/no): ",
                amount,
                account.getAccountNumber(), account.getClient().getName(),
                destinationAccount.getAccountNumber(), destinationAccount.getClient().getName()
            );

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
    
    public static void clientRefundRequestSection(Scanner scanner, Account account, Client client) {
        
    	RefundRequestService refundRequestService = new RefundRequestService();

        System.out.println("\n=== Refund Request Menu ===");
        System.out.println("1. Request a refund");
        System.out.println("2. View your refund requests");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
            	StatementService statementService = new StatementService();

                ZoneId zone = ZoneId.of("America/Sao_Paulo");

                List<Operation> transfers = statementService.viewTransferStatement(account, LocalDateTime.MIN, LocalDateTime.now(zone));

                if (transfers.isEmpty()) {
                    System.out.println("\n\nNo transfers found for your account.");
                    return;
                }

                System.out.println("\n\n=== Your Transfers ===");
                for (Operation op : transfers) {
                    System.out.printf("ID: %d | Type: %s | Amount: %.2f | Date: %s%n",
                        op.getId(), op.getOperationType(), op.getAmount(), op.getOperationDate());
                }

                System.out.print("\n\nEnter the transaction ID for the refund: ");
                String idInput = scanner.nextLine().trim();
                long operationId;

                try {
                    operationId = Long.parseLong(idInput);
                } catch (NumberFormatException e) {
                    System.out.println("\n\nInvalid transaction ID. Operation cancelled.");
                    return;
                }

                try {
                    refundRequestService.requestRefund(operationId, client.getId());
                    System.out.println("Refund request submitted successfully.");
                } catch (Exception e) {
                    String message = e.getMessage().toLowerCase();
                    if (message.contains("duplicate entry") || message.contains("uk_s5jjoys667jtxsew6vbdmimy1")) {
                        System.out.println("\n\nA refund has already been requested for this transaction.");
                    } else {
                        System.out.println("\n\nFailed to request refund: " + e.getMessage());
                    }
                }
                break;


            case 2:
                var requests = new RefundRequestRepository().findByClientId(client.getId());

                if (requests == null || requests.isEmpty()) {
                    System.out.println("\n\nYou have no refund requests.");
                    System.out.print("\n\nPress Enter to continue: ");
                    scanner.nextLine();
                } else {
                    System.out.println("\n\n=== Your Refund Requests ===");
                    for (var req : requests) {
                        System.out.printf("ID: %d | Operation ID: %d | Status: %s | Requested on: %s%n",
                                req.getId(),
                                req.getOperation().getId(),
                                req.getStatus(),
                                req.getRequestDate());
                    }
                    System.out.print("\n\nPress Enter to continue: ");
                    scanner.nextLine();
                }
                break;

            default:
                System.out.println("Invalid option.");
        }
    }
    
    public static void bankStatementSection(Scanner scanner, Client client, Account account) { 
        
    	System.out.println("\n\n=== Bank Statement Menu ===");
        System.out.println("0. Exit");
        System.out.println("1. View general statement");
        System.out.println("2. View deposit statement");
        System.out.println("3. View withdraw statement");
        System.out.println("4. View transfer statement");
        System.out.print("Choose an option: ");

        String input = scanner.nextLine().trim();
        int option;

        try {
            option = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("\n\nInvalid input. Please enter a number between 0 and 4.");
            return;
        }

        if (option < 0 || option > 4) {
            System.out.println("\n\nInvalid option. Please choose a number between 0 and 4.");
            return;
        }

        if (option == 0) {
            exitSection(scanner);
            return;
        }

        System.out.print("\n\nEnter period start (e.g., 01/04/2025 00:00): ");
        LocalDateTime start;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            start = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use dd/MM/yyyy HH:mm");
            return;
        }

        System.out.print("Enter period end (e.g., 10/04/2025 23:59): ");
        LocalDateTime end;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            end = LocalDateTime.parse(scanner.nextLine().trim(), formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use dd/MM/yyyy HH:mm");
            return;
        }

        if (!start.isBefore(end)) {
            System.out.println("Start date must be before end date.");
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        if (start.isAfter(now) || end.isAfter(now)) {
            System.out.println("\n\nDates cannot be in the future.");
            return;
        }

        StatementService service = new StatementService();
        List<Operation> operations = new ArrayList<>();

        switch (option) {
            case 1:
                operations = service.viewStatement(account.getId(), start, end);
                break;
            case 2:
                operations = service.viewDepositStatement(account.getId(), start, end);
                break;
            case 3:
                operations = service.viewWithdrawalStatement(account.getId(), start, end);
                break;
            case 4:
                operations = service.viewTransferStatement(account, start, end);
                break;
        }

        if (operations.isEmpty()) {
            System.out.println("\n\nNo operations found for your account in the given period.");
            return;
        }

        System.out.println("\n\n=== Statement Result ===");
        for (Operation op : operations) {
            System.out.printf("ID: %d | Type: %s | Amount: %.2f | Date: %s%n",
                op.getId(), op.getOperationType(), op.getAmount(), op.getOperationDate());
        }

        System.out.print("\nDo you want to export this statement to CSV? (yes/no): ");
        String exportAnswer = scanner.nextLine().trim().toLowerCase();

        if (exportAnswer.equals("yes") || exportAnswer.equals("y")) {
            String folderPath = "statements";
            File folder = new File(folderPath);
            if (!folder.exists())
                folder.mkdirs();

            String uniqueId = String.valueOf(System.currentTimeMillis());
            String fileName = client.getId() + "_" + uniqueId + "_statement.csv";
            String filePath = folderPath + File.separator + fileName;

            boolean success = service.exportStatementToCSV(operations, filePath);
            if (success) {
                System.out.println("\n\nCSV exported successfully to: " + filePath);
            } else {
                System.out.println("\n\nFailed to export CSV.");
            }
        }
    }
    
    public static void createNewAccount(Scanner scanner, Client client) {
    	
    	System.out.println("\n=== New Account ===");

        System.out.println("Select account type:");
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        System.out.println("3. Payroll");

        System.out.print("\nChoose an option: ");
        String input = scanner.nextLine().trim();

        int accountTypeChoice;
        try {
            accountTypeChoice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("\n\nInvalid input. Please enter a number between 1 and 3.");
            return;
        }

        AccountType accountType;

        switch (accountTypeChoice) {
            case 1:
                accountType = AccountType.CHECKING;
                break;
            case 2:
                accountType = AccountType.SAVINGS;
                break;
            case 3:
                accountType = AccountType.PAYROLL;
                break;
            default:
                System.out.println("\n\nInvalid account type selected. Defaulting to CHECKING.");
                accountType = AccountType.CHECKING;
        }

        try {
            Account newAccount = new Account(client, accountType);
            client.addAccount(newAccount);

            AccountService accountService = new AccountService();
            accountService.registerAccount(client, accountType);

            System.out.println("\n\nNew account creation successful!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n\nError creating account: " + e.getMessage());
        }
        
    }
        
    public static void checkBalanceSection(Scanner scanner, Account account) {
    	        
        AccountRepository accountRepository = new AccountRepository();
        Account refreshedAccount = accountRepository.findById(account.getId());
    	
        System.out.println("\n\n=== Check Balance ===");
        System.out.println("\nAccount Details:");
        System.out.printf("Account Number: %s%n", refreshedAccount.getAccountNumber());
        System.out.printf("Account Owner: %s%n", refreshedAccount.getClient().getName());
        System.out.printf("Current Balance: $ %.2f%n", refreshedAccount.getBalance());
        
        System.out.print("\n\nPress Enter to continue: ");
        scanner.nextLine();
    }
    
// Auxiliary methods ##########################################################################################
    
    private static Account refreshAccount(Account account) { 	
        return new AccountRepository().findById(account.getId());
    }
    
}
