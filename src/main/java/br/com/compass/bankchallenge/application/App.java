package br.com.compass.bankchallenge.application;

import java.time.LocalDate;
import java.util.Scanner;

import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.User;
import br.com.compass.bankchallenge.domain.enums.AccessLevel;
import br.com.compass.bankchallenge.service.AuthService;

public class App {
    
    public static void main(String[] args) {
       
    	/*
    	Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        System.out.println("Application closed");
 		*/
    	
    	Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();

        Client testClient = new Client();
        testClient.setName("João Teste");
        testClient.setEmail("joao@email.com");
        testClient.setPassword("1234"); 
        testClient.setCpf("12345678900");
        testClient.setPhone("11999999999");
        testClient.setBirthDate(LocalDate.of(1990, 1, 1));
        testClient.setAccessLevel(AccessLevel.CLIENT);
        testClient.setBlocked(false);
        testClient.setFailedLoginAttempts(0);

        authService.register(testClient); 

        System.out.println("\n=== Login Test ===");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User loggedUser = authService.login(email, password);

        if (loggedUser != null) {
            System.out.println("Login successful! Welcome, " + loggedUser.getName());
        } else {
            System.out.println("Login failed! Check email/password.");
        }

        authService.close();
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

            switch (option) {
                case 1:
                    bankMenu(scanner);
                    return;
                case 2:
                    // ToDo...
                    System.out.println("Account Opening.");
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
    
}
