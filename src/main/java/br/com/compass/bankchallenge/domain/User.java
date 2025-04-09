package br.com.compass.bankchallenge.domain;

import jakarta.persistence.*;

import br.com.compass.bankchallenge.domain.enums.AccessLevel;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tb_users", uniqueConstraints = { @UniqueConstraint(columnNames = "email")})
public abstract class User {
	
	public User() {	}
	
	public User(String name, String email, String password) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	private Integer failedLoginAttempts = 0;

	private String name;

	@Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
	private Boolean blocked = false;
	private AccessLevel accessLevel;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getFailedLoginAttempts() {
		return failedLoginAttempts;
	}
	
	public void setFailedLoginAttempts(Integer failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public AccessLevel getAccessLevel() {
		return accessLevel;
	}
	
	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}
	
}
