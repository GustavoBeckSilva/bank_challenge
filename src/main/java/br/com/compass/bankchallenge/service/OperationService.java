package br.com.compass.bankchallenge.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Operation;
import br.com.compass.bankchallenge.domain.enums.OperationType;
import br.com.compass.bankchallenge.repository.AccountRepository;
import br.com.compass.bankchallenge.repository.OperationRepository;

public class OperationService {

	 private OperationRepository operationRepository = new OperationRepository();
     private AccountRepository accountRepository = new AccountRepository();

	    public void createOperation(Operation operation) {
	        operation.setOperationDate(LocalDateTime.now());
	        operationRepository.save(operation);
	    }

	    public Operation getOperationById(Long id) {
	        Operation operation = operationRepository.findById(id);
	        if (operation == null) {
	            throw new IllegalArgumentException("Operation not found with id: " + id);
	        }
	        return operation;
	    }
	    
	    public List<Operation> getOperationsByAccountId(Long accountId) {
	        List<Operation> operations = operationRepository.findByAccountId(accountId);
	        return operations;
	    }
	    
	    public void withdrawal(Long accountId, Double amount) {
	        Account account = accountRepository.findById(accountId);
	        
	        if (account.getBalance() < amount)
	            throw new IllegalArgumentException("Insufficient balance for withdrawal.");
	        
	        account.setBalance(account.getBalance() - amount);
	        accountRepository.save(account);
	        
	        Operation op = new Operation();
	        op.setAccount(account);
	        op.setAmount(amount);
	        op.setOperationType(OperationType.WITHDRAWAL);
	        op.setOperationDate(LocalDateTime.now());
	        operationRepository.save(op);
	    }

	    public void deposit(Long accountId, Double amount) {
	        
	    	Account account = accountRepository.findById(accountId);
	        account.setBalance(account.getBalance() + amount);
	        accountRepository.save(account); 
	        
	        Operation op = new Operation();
	        op.setAccount(account);
	        op.setAmount(amount);
	        op.setOperationType(OperationType.DEPOSIT);
	        op.setOperationDate(LocalDateTime.now());
	        operationRepository.save(op);
	    }

	    public void transfer(Long sourceAccountId, Long destinationAccountId, Double amount) {
	        
	    	Account sourceAccount = accountRepository.findById(sourceAccountId);
	        Account destinationAccount = accountRepository.findById(destinationAccountId);

	        if (sourceAccount.getBalance() < amount)
	            throw new IllegalArgumentException("Insufficient balance for transfer.");

	        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
	        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

	        accountRepository.save(sourceAccount);
	        accountRepository.save(destinationAccount);

	        Operation opTransfer = new Operation();
	        opTransfer.setAccount(sourceAccount);
	        opTransfer.setTargetAccount(destinationAccount);
	        opTransfer.setAmount(amount);
	        opTransfer.setOperationType(OperationType.TRANSFER);
	        opTransfer.setOperationDate(LocalDateTime.now());

	        operationRepository.save(opTransfer);
	        

	    }
	    
}
