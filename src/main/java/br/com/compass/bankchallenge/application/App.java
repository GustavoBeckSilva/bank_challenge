package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.util.Scanner;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.repository.UserRepository;
import br.com.compass.bankchallenge.service.AccountService;
import br.com.compass.bankchallenge.service.AuthService;
import br.com.compass.bankchallenge.service.ClientService;
import br.com.compass.bankchallenge.service.ManagerService;

public class App {
    
    public static void main(String[] args) {	
    	    	
    	/*
        
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        System.out.println("Application closed");
        
        */
    	
    	 // Instâncias dos serviços
        ClientService clientService = new ClientService();
        ManagerService managerService = new ManagerService();
        AccountService accountService = new AccountService();
        
        // Variáveis de teste para os dados das entidades
        String clientName = "Cliente Teste";
        String clientEmail = "client@test.com";
        String clientPassword = "senhaClient";
        String clientCpf = "12345678901";
        String clientPhone = "1199998888";
        LocalDate clientBirthDate = LocalDate.of(1995, 5, 15);
        
        String managerName = "Manager Teste";
        String managerEmail = "manager@test.com";
        String managerPassword = "senhaManager";
        
        String accountNumber = "ACC-001";
        Double initialBalance = 1000.0;
        
        // Registra o Client
        try {
            clientService.registerClient(clientName, clientEmail, clientPassword, clientCpf, clientPhone, clientBirthDate);
            System.out.println("Client registered successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Client registration failed: " + e.getMessage());
        }
        
        // Registra o Manager
        try {
            managerService.registerManager(managerName, managerEmail, managerPassword);
            System.out.println("Manager registered successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Manager registration failed: " + e.getMessage());
        }
        
        // Para criar uma conta, precisamos do Client salvo.
        // Usamos o UserRepository para recuperar o client pelo e-mail.
        UserRepository userRepository = new UserRepository();
        Client client = (Client) userRepository.findByEmail(clientEmail);
        
        if (client != null) {
            accountService.registerAccount(accountNumber, initialBalance, client, AccountType.SAVINGS);
            System.out.println("Account registered successfully for client: " + client.getName());
        } else {
            System.out.println("Client not found. Account registration aborted.");
        }
        
        // Teste simples concluído
        System.out.println("Test finished.");
       
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
                	loginSection(scanner);
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
        
    	accountSelectionSection(scanner);    	

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
    
    public static void loginSection(Scanner scanner) {

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
    }
    
    public static void accountOpeningSection(Scanner scanner) { 
    	
        System.out.println("Account Opening.");
    	
    }
    
    public static void accountSelectionSection(Scanner scanner) {
    	
    }


    
}
