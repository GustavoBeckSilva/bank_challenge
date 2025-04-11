package br.com.compass.bankchallenge.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Operation;
import br.com.compass.bankchallenge.domain.Statement;
import br.com.compass.bankchallenge.domain.enums.OperationType;
import br.com.compass.bankchallenge.repository.OperationRepository;
import br.com.compass.bankchallenge.repository.StatementRepository;

public class StatementService {

	private final AccountService accountService = new AccountService();
    private final OperationRepository operationRepository = new OperationRepository();
    private final StatementRepository statementRepository = new StatementRepository();
   
    
    
    public List<Operation> viewStatement(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd) {

        List<Operation> allOps = operationRepository.findByAccountId(accountId);
        if (allOps == null) {
            System.out.println("No operations found for account " + accountId);
            return null;
        }
        return allOps.stream()
                .filter(op -> !op.getOperationDate().isBefore(periodStart) &&
                              !op.getOperationDate().isAfter(periodEnd))
                .collect(Collectors.toList());
    
    }
    
    public List<Operation> viewWithdrawalStatement(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd) {
    	List<Operation> withdrawalOps = viewStatement(accountId, periodStart, periodEnd).stream()
                .filter(op -> op.getOperationType() == OperationType.WITHDRAWAL)
                .collect(Collectors.toList());
        if (withdrawalOps.isEmpty()) {
            System.out.println("No withdrawal operations found for account " + accountId + " in the given period.");
        }
        return withdrawalOps;
    }

    public List<Operation> viewDepositStatement(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd) {
    	List<Operation> depositOps = viewStatement(accountId, periodStart, periodEnd).stream()
                .filter(op -> op.getOperationType() == OperationType.DEPOSIT)
                .collect(Collectors.toList());
        if (depositOps.isEmpty()) {
            System.out.println("No deposit operations found for account " + accountId + " in the given period.");
        }
        return depositOps;
    }

    public List<Operation> viewTransferStatement(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd) {

        List<Operation> transferSourceOps = viewStatement(accountId, periodStart, periodEnd).stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER)
                .collect(Collectors.toList());

        List<Operation> transferReceivedOps = operationRepository.findByTargetAccountId(accountId).stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER &&
                              !op.getOperationDate().isBefore(periodStart) &&
                              !op.getOperationDate().isAfter(periodEnd))
                .collect(Collectors.toList());

        transferSourceOps.addAll(transferReceivedOps);

        if (transferSourceOps.isEmpty()) {
            System.out.println("No transfer operations found for account " + accountId + " in the given period.");
        }
        return transferSourceOps;
    }
    
    public boolean exportStatementToCSV(List<Operation> operations, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("ID,Type,Amount,Date");

            for (Operation op : operations) {
                writer.printf("%d,%s,%.2f,%s%n",
                    op.getId(),
                    op.getOperationType().name(),
                    op.getAmount(),
                    op.getOperationDate().toString()
                );
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Statement exportStatementToCSV(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd, String filePath) {
        
    	List<Operation> operations = viewStatement(accountId, periodStart, periodEnd);
        if (operations == null || operations.isEmpty()) {
            System.out.println("No operations found for account " + accountId + " in the given period.");
            return null;
        }

        boolean exportSuccess = exportOperationsToCSV(operations, filePath);
        if (!exportSuccess) {
            System.out.println("Error generating CSV file.");
            return null;
        }

        Statement statement = new Statement();

        statement.setAccount(operations.get(0).getAccount());
        statement.setPeriodStart(periodStart);
        statement.setPeriodEnd(periodEnd);
        statement.setGeneratedDate(LocalDateTime.now());
        statement.setCsvFilePath(filePath);

        statementRepository.save(statement);
        return statement;
    }
    
    private boolean exportOperationsToCSV(List<Operation> operations, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("OperationID,Type,Amount,Date\n");
            for (Operation op : operations) {
                writer.write(String.format("%d,%s,%.2f,%s\n",
                        op.getId(),
                        op.getOperationType().name(),
                        op.getAmount(),
                        op.getOperationDate().toString()));
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Statement createStatementRecord(Long accountId, LocalDateTime periodStart, LocalDateTime periodEnd, String filePath, List<Operation> operations) {
    	 if (operations == null || operations.isEmpty()) {
    	        System.out.println("No operations to record in statement.");
    	        return null;
    	    }

    	    Account account = operations.get(0).getAccount(); 

    	    Statement statement = new Statement();
    	    statement.setAccount(account);
    	    statement.setPeriodStart(periodStart);
    	    statement.setPeriodEnd(periodEnd);
    	    statement.setGeneratedDate(LocalDateTime.now());
    	    statement.setCsvFilePath(filePath);

    	    accountService.addStatementToAccount(account, statement);

    	    return statement;
    }
    
}