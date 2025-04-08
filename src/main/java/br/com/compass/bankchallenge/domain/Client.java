package br.com.compass.bankchallenge.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@Table(name = "tb_clients")
public class Client extends User{

	private String cpf;
	private String phone;
	private LocalDate birthDate;

	//private List<Account> accounts;
	
	public Client() {}

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

/*	public List<Account> getAccounts() {
		return accounts;
	}
*/	
}
