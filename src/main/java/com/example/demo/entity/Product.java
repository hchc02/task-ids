package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends AbstractAuditingEntity {
    
    @Id
    @Column(name = "product_id", length = 10)
    private String productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
