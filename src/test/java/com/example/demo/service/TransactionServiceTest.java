package com.example.demo.service;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransactionResponseDto;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.TransactionStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionStatusRepository transactionStatusRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction1;
    private Transaction transaction2;
    private Product product1;
    private Product product2;
    private Customer customer;
    private TransactionStatus successStatus;
    private TransactionStatus failedStatus;
    private Page<Transaction> transactionPage;
    private List<TransactionStatus> statusList;

    @BeforeEach
    void setUp() {
        // Setup test data
        product1 = new Product();
        product1.setProductId("10001");
        product1.setProductName("Test 1");

        product2 = new Product();
        product2.setProductId("10002");
        product2.setProductName("Test 2");

        customer = new Customer();
        customer.setCustomerId(1);
        customer.setCustomerName("abc");

        successStatus = new TransactionStatus();
        successStatus.setStatusId(0);
        successStatus.setStatusName("SUCCESS");

        failedStatus = new TransactionStatus();
        failedStatus.setStatusId(1);
        failedStatus.setStatusName("FAILED");

        // Create test transactions
        transaction1 = new Transaction();
        transaction1.setId(1372);
        transaction1.setProduct(product1);
        transaction1.setAmount(new BigDecimal("1000"));
        transaction1.setCustomer(customer);
        transaction1.setTransactionStatus(successStatus);
        transaction1.setTransactionDate(Instant.parse("2022-07-10T11:14:52Z"));
        transaction1.setCreatedBy("abc");
        transaction1.setCreatedAt(Instant.parse("2022-07-10T11:14:52Z"));

        transaction2 = new Transaction();
        transaction2.setId(1373);
        transaction2.setProduct(product2);
        transaction2.setAmount(new BigDecimal("2000"));
        transaction2.setCustomer(customer);
        transaction2.setTransactionStatus(failedStatus);
        transaction2.setTransactionDate(Instant.parse("2022-07-11T13:14:52Z"));
        transaction2.setCreatedBy("abc");
        transaction2.setCreatedAt(Instant.parse("2022-07-10T13:14:52Z"));

        // Setup page and status list
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        transactionPage = new PageImpl<>(transactions, PageRequest.of(0, 15), transactions.size());
        statusList = Arrays.asList(successStatus, failedStatus);
    }

    @Test
    void getTransactionsWithStatus_ShouldReturnTransactionResponseDto() {
        // Given
        int page = 0;
        int size = 15;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        when(transactionRepository.TransactionWithDetails(expectedPageable)).thenReturn(transactionPage);
        when(transactionStatusRepository.findAll()).thenReturn(statusList);

        // When
        TransactionResponseDto result = transactionService.getTransactionsWithStatus(page, size);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getStatus());
        
        // Verify data content
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getStatus().size());
        
        // Verify first transaction
        TransactionDto firstTransaction = result.getData().get(0);
        assertEquals(1372, firstTransaction.getId());
        assertEquals("10001", firstTransaction.getProductID());
        assertEquals("Test 1", firstTransaction.getProductName());
        assertEquals("1000", firstTransaction.getAmount());
        assertEquals("abc", firstTransaction.getCustomerName());
        assertEquals(0, firstTransaction.getStatus());
        assertEquals("abc", firstTransaction.getCreateBy());
        assertNotNull(firstTransaction.getTransactionDate());
        assertNotNull(firstTransaction.getCreateOn());
        
        // Verify second transaction
        TransactionDto secondTransaction = result.getData().get(1);
        assertEquals(1373, secondTransaction.getId());
        assertEquals("10002", secondTransaction.getProductID());
        assertEquals("Test 2", secondTransaction.getProductName());
        assertEquals("2000", secondTransaction.getAmount());
        assertEquals("abc", secondTransaction.getCustomerName());
        assertEquals(1, secondTransaction.getStatus());
        
        // Verify status mappings
        List<TransactionResponseDto.StatusDto> statuses = result.getStatus();
        assertEquals(0, statuses.get(0).getId());
        assertEquals("SUCCESS", statuses.get(0).getName());
        assertEquals(1, statuses.get(1).getId());
        assertEquals("FAILED", statuses.get(1).getName());
        
        // Verify method calls
        verify(transactionRepository, times(1)).TransactionWithDetails(expectedPageable);
        verify(transactionStatusRepository, times(1)).findAll();
    }

    @Test
    void getTransactionsWithStatus_WithCustomPageSize_ShouldUseCorrectPagination() {
        // Given
        int page = 1;
        int size = 10;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        List<Transaction> customTransactions = Arrays.asList(transaction1);
        Page<Transaction> customPage = new PageImpl<>(customTransactions, expectedPageable, 1);
        
        when(transactionRepository.TransactionWithDetails(expectedPageable)).thenReturn(customPage);
        when(transactionStatusRepository.findAll()).thenReturn(statusList);

        // When
        TransactionResponseDto result = transactionService.getTransactionsWithStatus(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(2, result.getStatus().size());
        
        verify(transactionRepository, times(1)).TransactionWithDetails(expectedPageable);
        verify(transactionStatusRepository, times(1)).findAll();
    }

    @Test
    void getTransactionsWithStatus_WithEmptyResult_ShouldReturnEmptyData() {
        // Given
        int page = 0;
        int size = 15;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        Page<Transaction> emptyPage = new PageImpl<>(Arrays.asList(), expectedPageable, 0);
        
        when(transactionRepository.TransactionWithDetails(expectedPageable)).thenReturn(emptyPage);
        when(transactionStatusRepository.findAll()).thenReturn(statusList);

        // When
        TransactionResponseDto result = transactionService.getTransactionsWithStatus(page, size);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getStatus());
        assertEquals(0, result.getData().size());
        assertEquals(2, result.getStatus().size());
        
        verify(transactionRepository, times(1)).TransactionWithDetails(expectedPageable);
        verify(transactionStatusRepository, times(1)).findAll();
    }

    @Test
    void getTransactionsWithStatus_WithNoStatuses_ShouldReturnEmptyStatusList() {
        // Given
        int page = 0;
        int size = 15;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        when(transactionRepository.TransactionWithDetails(expectedPageable)).thenReturn(transactionPage);
        when(transactionStatusRepository.findAll()).thenReturn(Arrays.asList());

        // When
        TransactionResponseDto result = transactionService.getTransactionsWithStatus(page, size);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getStatus());
        assertEquals(2, result.getData().size());
        assertEquals(0, result.getStatus().size());
        
        verify(transactionRepository, times(1)).TransactionWithDetails(expectedPageable);
        verify(transactionStatusRepository, times(1)).findAll();
    }

    @Test
    void convertToDTO_ShouldCorrectlyMapAllFields() {
        // This test is for the private method, we can test it indirectly through the public method
        // Given
        int page = 0;
        int size = 15;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        when(transactionRepository.TransactionWithDetails(expectedPageable)).thenReturn(transactionPage);
        when(transactionStatusRepository.findAll()).thenReturn(statusList);

        // When
        TransactionResponseDto result = transactionService.getTransactionsWithStatus(page, size);

        // Then
        TransactionDto dto = result.getData().get(0);
        
        // Verify all fields are correctly mapped
        assertEquals(transaction1.getId(), dto.getId());
        assertEquals(transaction1.getProduct().getProductId(), dto.getProductID());
        assertEquals(transaction1.getProduct().getProductName(), dto.getProductName());
        assertEquals(String.valueOf(transaction1.getAmount().intValue()), dto.getAmount());
        assertEquals(transaction1.getCustomer().getCustomerName(), dto.getCustomerName());
        assertEquals(transaction1.getTransactionStatus().getStatusId(), dto.getStatus());
        assertEquals(transaction1.getCreatedBy(), dto.getCreateBy());
        
        // Verify date conversion (should be converted to Asia/Jakarta timezone)
        LocalDateTime expectedTransactionDate = LocalDateTime.ofInstant(
            transaction1.getTransactionDate(), ZoneId.of("Asia/Jakarta"));
        LocalDateTime expectedCreatedOn = LocalDateTime.ofInstant(
            transaction1.getCreatedAt(), ZoneId.of("Asia/Jakarta"));
        
        assertEquals(expectedTransactionDate, dto.getTransactionDate());
        assertEquals(expectedCreatedOn, dto.getCreateOn());
    }

    @Test
    void getTransactionsWithStatus_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        int page = 0;
        int size = 15;
        PageRequest expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        
        when(transactionRepository.TransactionWithDetails(expectedPageable))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionsWithStatus(page, size);
        });
        
        verify(transactionRepository, times(1)).TransactionWithDetails(expectedPageable);
        verify(transactionStatusRepository, never()).findAll();
    }
}
