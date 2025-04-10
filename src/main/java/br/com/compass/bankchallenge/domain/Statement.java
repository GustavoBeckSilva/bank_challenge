package br.com.compass.bankchallenge.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_statements")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
 
    private LocalDateTime generatedDate;
    
    private String csvFilePath;

    public Statement() {}
    
    public Statement(Account account, LocalDateTime periodStart, LocalDateTime periodEnd, LocalDateTime generatedDate, String csvFilePath) {
		this.account = account;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.generatedDate = generatedDate;
		this.csvFilePath = csvFilePath;
	}

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
    
    public LocalDateTime getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }
    
    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }
    
    public String getCsvFilePath() {
        return csvFilePath;
    }
    
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
    
}
