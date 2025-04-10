package br.com.compass.bankchallenge.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.compass.bankchallenge.domain.enums.AccountType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_accounts")
public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "account_number", nullable = false, unique = true)
	private String accountNumber;
	
    @Column(nullable = false)
	private Double balance;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
	private Client client;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
	private AccountType accountType;
	
	// private List<Statement> statments;
	// private List<RefundRequest> refundRequests;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Operation> operations = new ArrayList<>();
	
	public Account() {}
	
	public Account(String accountNumber, Double balance, Client client, AccountType accountType) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.client = client;
		this.accountType = accountType;
	}
	
	public Account(Client client, AccountType accountType) {
        this.client = client;
        this.accountType = accountType;
        this.balance = 0.0;
        this.accountNumber = generateAccountNumber();
    }

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
		
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Double getBalance() {
		return balance;
	}
	
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public AccountType getAccountType() {
		return accountType;
	}
	
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	
	public List<Operation> getOperations() {
        return operations;
    }
	
	// Specific methods
	
	private String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
	
	public void addOperation(Operation operation) {
        operations.add(operation);
        operation.setAccount(this);
    }
    
    public void removeOperation(Operation operation) {
        operations.remove(operation);
        operation.setAccount(null);
    }
	
}
