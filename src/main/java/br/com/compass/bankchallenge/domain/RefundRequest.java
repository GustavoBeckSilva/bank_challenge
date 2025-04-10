package br.com.compass.bankchallenge.domain;

import java.time.LocalDateTime;

import br.com.compass.bankchallenge.domain.enums.RefundStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_refund_request")
public class RefundRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @OneToOne(optional = false)
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private LocalDateTime requestDate;

    private LocalDateTime responseDate;
	
    public RefundRequest() {}
    
    
	
}
