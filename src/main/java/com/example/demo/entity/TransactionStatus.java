package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "transaction_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatus extends AbstractAuditingEntity {
    
    @Id
    @Column(name = "status_id")
    private Integer statusId;
    
    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;
    
    @OneToMany(mappedBy = "transactionStatus", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
