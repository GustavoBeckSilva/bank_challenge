package br.com.compass.bankchallenge.domain;

import java.time.LocalDateTime;

import br.com.compass.bankchallenge.domain.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_operations")
public class Operation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;
	
	@ManyToOne
	@JoinColumn(name = "target_account_id")
	private Account targetAccount;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
    private OperationType operationType;

    private Double amount;

    private LocalDateTime operationDate;
    
    public Operation() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDateTime getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(LocalDateTime operationDate) {
		this.operationDate = operationDate;
	}
	
	public Account getTargetAccount() {
		return targetAccount;
	}
	
	public void setTargetAccount(Account targetAccount) {
		this.targetAccount = targetAccount;
	}
    	
}
