package br.com.compass.bankchallenge.domain;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client extends User{

	private String cpf;
	private String phone;
	
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
	
}
