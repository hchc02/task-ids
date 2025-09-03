package com.example.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransactionResponseDto;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.TransactionStatusRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionStatusRepository transactionStatusRepository;

    @Transactional
    public TransactionResponseDto getTransactionsWithStatus(int page, int size) {
        log.info("Fetching transactions with status mapping");
        
        Page<TransactionDto> transactionDtos = getAllTransactions(page, size);
        
        List<TransactionStatus> statuses = transactionStatusRepository.findAll();
        List<TransactionResponseDto.StatusDto> statusDtos = statuses.stream()
                .map(status -> new TransactionResponseDto.StatusDto(status.getStatusId(), status.getStatusName()))
                .collect(Collectors.toList());
        
        return new TransactionResponseDto(transactionDtos.getContent(), statusDtos);
    }
 
    private Page<TransactionDto> getAllTransactions(int page, int size) {
        log.info("Fetching all transactions");
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id"));
        return transactionRepository.TransactionWithDetails(pageable).map(this::convertToDTO);
    }

    private TransactionDto convertToDTO(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setProductID(transaction.getProduct().getProductId());
        dto.setProductName(transaction.getProduct().getProductName());
        dto.setAmount(String.valueOf(transaction.getAmount().intValue()));
        dto.setCustomerName(transaction.getCustomer().getCustomerName());
        dto.setStatus(transaction.getTransactionStatus().getStatusId());
        dto.setTransactionDate(instantToLocalDateTime(transaction.getTransactionDate()));
        dto.setCreateBy(transaction.getCreatedBy());
        dto.setCreateOn(instantToLocalDateTime(transaction.getCreatedAt()));
        return dto;
    }
    
    private LocalDateTime instantToLocalDateTime(Instant instantDateTime) {
        return LocalDateTime.ofInstant(instantDateTime, ZoneId.of("Asia/Jakarta"));
    }
}
