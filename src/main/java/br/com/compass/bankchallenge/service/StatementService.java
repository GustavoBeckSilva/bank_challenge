package br.com.compass.bankchallenge.service;

import br.com.compass.bankchallenge.repository.OperationRepository;
import br.com.compass.bankchallenge.repository.StatementRepository;

public class StatementService {

    private final OperationRepository operationRepository = new OperationRepository();
    private final StatementRepository statementRepository = new StatementRepository();

    
}