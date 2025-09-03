package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    @Query(
    value = """
        SELECT t FROM Transaction t
        LEFT JOIN FETCH t.product
        LEFT JOIN FETCH t.customer
        LEFT JOIN FETCH t.transactionStatus
        """,
    countQuery = "SELECT COUNT(t) FROM Transaction t")
    Page<Transaction> TransactionWithDetails(Pageable pageable);
}
