package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Client;
import br.com.compass.bankchallenge.domain.Statement;
import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.repository.AccountRepository;

public class AccountService {

    private AccountRepository accountRepository = new AccountRepository();

    public void registerAccount(String accountNumber, Double balance, Client client, AccountType accountType) {
        Account account = new Account(accountNumber, balance, client, accountType);
        accountRepository.save(account);
    }
    
    public Account updateAccount(Account account) {
        return new AccountRepository().save(account);
    }
    
    public void registerAccount(Client client, AccountType accountType) {
        Account account = new Account(client, accountType);
        accountRepository.save(account);
    }
    
    public void addStatementToAccount(Account account, Statement statement) {
        account.addStatement(statement); 
        accountRepository.save(account);     
    }
    
    
}