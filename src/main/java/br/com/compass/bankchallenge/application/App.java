package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.util.Scanner;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.service.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {
    
    public static void main(String[] args) {	
    	    	
    	/*
        
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        System.out.println("Application closed");
        
        */
    	
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("exemplo-jpa");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Client client = new Client();
            client.setName("Cliente Teste");
            client.setEmail("cliente@teste.com");
            client.setPassword("1234");
            client.setCpf("11122233344");
            client.setPhone("11988887777");
            client.setBirthDate(LocalDate.of(1990, 1, 1));
            client.setAccessLevel(AccessLevel.CLIENT);
            
            em.persist(client);
            
            Account account = new Account();
            account.setAccountNumber("ACC-1001");  
            account.setBalance(1000.0);          
            account.setClient(client);
            account.setAccountType(AccountType.SAVINGS);
            
            em.persist(account);
            
            em.getTransaction().commit();
            
            System.out.println("Cliente e conta criados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
            emf.close();
        }
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
