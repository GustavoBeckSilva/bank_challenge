package br.com.compass.bankchallenge.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@Table(name = "tb_clients")
public class Client extends User{

    @Column(nullable = false, unique = true)
	private String cpf;
    
    @Column(nullable = false)
    private String phone;
	
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Account> accounts = new ArrayList<>();
	
	public Client() {
		super();
	}
	
	public Client(String name, String email, String password, String cpf, String phone, LocalDate birthDate) {
	    super(name, email, password);
	    this.cpf = cpf;
	    this.phone = phone;
	    this.birthDate = birthDate;
	}	

	public Client(String cpf, String phone, LocalDate birthDate) {
		super();
		this.cpf = cpf;
		this.phone = phone;
		this.birthDate = birthDate;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public List<Account> getAccounts() {
		return accounts;
	}
	
	// Specific methods
	
	public void addAccount(Account account) {
		this.accounts.add(account);
        account.setClient(this);
    }

}
