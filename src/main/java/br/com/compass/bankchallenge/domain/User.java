package br.com.compass.bankchallenge.domain;

import jakarta.persistence.*;

import br.com.compass.bankchallenge.domain.enums.AccessLevel;

@MappedSuperclass
public abstract class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private AccessLevel accessLevel;
	// private String password;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

}
