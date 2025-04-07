package br.com.compass.bankchallenge.domain;

import java.util.List;

import br.com.compass.bankchallenge.domain.enums.AccountType;
import br.com.compass.bankchallenge.domain.Statement;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Double balance;
	private Client client;
	private AccountType accountType;
	
	private List<Statement> statments;
	private List<RefundRequest> refundRequests;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	public List<Statement> getStatments() {
		return statments;
	}
	
	public void setStatments(List<Statement> statments) {
		this.statments = statments;
	}
	
	public List<RefundRequest> getRefundRequests() {
		return refundRequests;
	}
	
}
