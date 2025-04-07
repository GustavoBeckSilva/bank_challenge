package br.com.compass.bankchallenge.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Statement {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
