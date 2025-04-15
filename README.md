# BankChallenge

BankChallenge is a robust banking system that simulates financial operations and account management. It supports both customers and managers, utilizing a clean domain model along with JPA/Hibernate for persistence. The project follows a modular architecture and incorporates best practices for validations, security, and error handling.

## Overview

BankChallenge simulates a full-featured banking system where users can perform deposits, withdrawals, transfers, and even request refunds. The system is built in Java with a layered architecture that separates the domain, repository, service, and configuration layers.

---

## Features

### Unauthenticated Users

- **Login:**  
  Authenticate customers (via CPF) and managers (via email).

- **Open Account:**  
  Create a new customer profile and open an account (Checking, Savings, or Payroll).

### Authenticated Customers

- **Deposit:**  
  Add funds to an account.

- **Withdrawal:**  
  Withdraw funds from an account with balance validation.

- **Transfer:**  
  Transfer money between accounts with destination and balance validations.

- **Bank Statement:**  
  Generate account statements for a given period, with CSV export support.

- **Refund Request:**  
  Request a refund for transfers.

- **New Account:**  
  Create additional accounts linked to the customer profile.

### Managers

- **Refund Approval:**  
  Process pending refund requests (approve/reject).

- **Account Unlock:**  
  Unlock accounts blocked after multiple failed login attempts.

- **Manager Creation:**  
  Register new managers (restricted to master managers).

---

## Project Structure

![CD drawio](https://github.com/user-attachments/assets/526a9e84-9157-4c77-ae6d-904e5b2b0e07)

```plaintext
BankChallenge/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br.com.compass.bankchallenge/
│   │   │       ├── application/
│   │   │       │   └── App.java
│   │   │       ├── config/
│   │   │       │   └── DatabaseInitializer.java
│   │   │       ├── domain/
│   │   │       │   ├── Account.java
│   │   │       │   ├── Client.java
│   │   │       │   ├── Manager.java
│   │   │       │   ├── Operation.java
│   │   │       │   ├── RefundRequest.java
│   │   │       │   ├── Statement.java
│   │   │       │   └── User.java
│   │   │       ├── domain/enums/
│   │   │       │   ├── AccessLevel.java
│   │   │       │   ├── AccountType.java
│   │   │       │   ├── OperationType.java
│   │   │       │   └── RefundStatus.java
│   │   │       ├── exceptions/
│   │   │       │   ├── BusinessLogicException.java
│   │   │       │   ├── DatabaseException.java
│   │   │       │   └── ValidationException.java
│   │   │       ├── repository/
│   │   │       │   ├── AccountRepository.java
│   │   │       │   ├── ClientRepository.java
│   │   │       │   ├── ManagerRepository.java
│   │   │       │   ├── OperationRepository.java
│   │   │       │   ├── RefundRequestRepository.java
│   │   │       │   ├── StatementRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── service/
│   │   │       │   ├── AccountService.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── ClientService.java
│   │   │       │   ├── ManagerService.java
│   │   │       │   ├── OperationService.java
│   │   │       │   ├── RefundRequestService.java
│   │   │       │   ├── StatementService.java
│   │   │       │   └── UserService.java
│   │   │       └── util/
│   │   │           ├── InputValidationUtil.java
│   │   │           ├── JPAUtil.java
│   │   │           └── SecurityUtil.java
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── persistence.xml
│   │       └── logging.properties
├── target/
└── pom.xml
````

## Database Schema

![MER drawio (1)](https://github.com/user-attachments/assets/a29365d7-0c1d-4bdd-9540-23e6416cb5bc)

---

### Relationships & Cardinalities

- **Inheritance:**
  - *tb_users* → *tb_clients*: **1:1** (via id)
  - *tb_users* → *tb_managers*: **1:1** (via id)

- **Clients and Accounts:**
  - *tb_clients* (1) → *tb_accounts* (N): One client can own multiple accounts.

- **Accounts and Operations:**
  - *tb_accounts* (1) → *tb_operations* (N): Each account can register multiple operations.  
    > **Note:** Operations may include an optional target account for transfers.

- **Operations and Refund Requests:**
  - *tb_operations* (1) → *tb_refund_requests* (1): Each operation may have a unique associated refund request.

- **Clients and Refund Requests:**
  - *tb_clients* (1) → *tb_refund_requests* (N): A client can submit multiple refund requests.

- **Managers and Refund Requests:**
  - *tb_managers* (0..1) → *tb_refund_requests* (N): A refund request may or may not be evaluated by a manager.

- **Accounts and Statements:**
  - *tb_accounts* (1) → *tb_statements* (N): An account may have many bank statements.

---

- **Enumerations:**  
  *AccessLevel*, *AccountType*, *OperationType*, and *RefundStatus* restrict the allowed values for their corresponding columns.

---

## Domain Model

### Key Entities

- **User:**  
  Base class containing fields like email, password, access level, blocked status, and failed login attempts.  
  *Subclasses:* Client, Manager

- **Client:**  
  Inherits from User and includes additional fields such as CPF, phone, and birth date.  

- **Account:**  
  Represents a bank account with properties such as account number, balance, and account type.  

- **Operation:**  
  Represents financial transactions (deposit, withdrawal, transfer) with attributes for amount, date, operation type, and, optionally, a target account for transfers.

- **RefundRequest:**  
  Represents a refund request with statuses (PENDING, APPROVED, REJECTED) and associations to an operation, client, and optionally a manager.

- **Statement:**  
  Represents a bank statement with details about the period, generation date, and CSV export path.

---

## Repositories & Services

### Repositories

- **AccountRepository:**  
  Methods: `save()`, `findById()`, `findByAccountNumber()`

- **ClientRepository:**  
  Methods: `save()` and other custom queries

- **ManagerRepository:**  
  Methods: `findByEmail()`, `save()`

- **OperationRepository:**  
  Methods: `findByAccountId(Long accountId)`, `findByTargetAccountId(Long targetAccountId)`

- **RefundRequestRepository:**  
  Methods: Searching by status (*PENDING*, *APPROVED*, *REJECTED*), updating requests, listing by client

- **StatementRepository:**  
  Methods: `save()`, `findByAccountId()`

- **UserRepository:**  
  Methods: `findByEmail(String email)`, `findByBlocked(boolean blocked)`, `update(User user)`

### Services

- **AuthService:**  
  Handles secure authentication and registration of users (customers and managers), including login attempts and account blocking.

- **ClientService:**  
  Manages customer registration, CPF validation, and automatic account linking.

- **AccountService:**  
  Manages account creation and updates.

- **OperationService:**  
  Processes financial transactions (withdrawal, deposit, transfer) with balance and limit validations.

- **RefundRequestService:**  
  Manages refund requests, including submission by clients and approval/rejection by managers.

- **StatementService:**  
  Generates account statements, filtering by period and type, and supports CSV export.

- **UserService:**  
  Handles user queries, including listing blocked users and updating user details.

---

## Utilities

- **InputValidationUtil:**  
  Validates various input types (CPF, email, phone, dates) according to specific patterns.

- **SecurityUtil:**  
  Provides password hashing using SHA-256, returning a secure hexadecimal string.

- **JPAUtil:**  
  Configures and manages the EntityManagerFactory for JPA/Hibernate integration.
