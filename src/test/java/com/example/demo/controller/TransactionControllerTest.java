package com.example.demo.controller;

import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransactionResponseDto;
import com.example.demo.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionResponseDto transactionResponseDto;
    private List<TransactionDto> transactionDtos;
    private List<TransactionResponseDto.StatusDto> statusDtos;

    @BeforeEach
    void setUp() {
        // Setup test data
        TransactionDto transactionDto1 = new TransactionDto();
        transactionDto1.setId(1372);
        transactionDto1.setProductID("10001");
        transactionDto1.setProductName("Test 1");
        transactionDto1.setAmount("1000");
        transactionDto1.setCustomerName("abc");
        transactionDto1.setStatus(0);
        transactionDto1.setTransactionDate(LocalDateTime.of(2022, 7, 10, 11, 14, 52));
        transactionDto1.setCreateBy("abc");
        transactionDto1.setCreateOn(LocalDateTime.of(2022, 7, 10, 11, 14, 52));

        TransactionDto transactionDto2 = new TransactionDto();
        transactionDto2.setId(1373);
        transactionDto2.setProductID("10002");
        transactionDto2.setProductName("Test 2");
        transactionDto2.setAmount("2000");
        transactionDto2.setCustomerName("abc");
        transactionDto2.setStatus(1);
        transactionDto2.setTransactionDate(LocalDateTime.of(2022, 7, 11, 13, 14, 52));
        transactionDto2.setCreateBy("abc");
        transactionDto2.setCreateOn(LocalDateTime.of(2022, 7, 10, 13, 14, 52));

        transactionDtos = Arrays.asList(transactionDto1, transactionDto2);

        // Setup status DTOs
        TransactionResponseDto.StatusDto successStatus = new TransactionResponseDto.StatusDto(0, "SUCCESS");
        TransactionResponseDto.StatusDto failedStatus = new TransactionResponseDto.StatusDto(1, "FAILED");
        statusDtos = Arrays.asList(successStatus, failedStatus);

        // Setup response DTO
        transactionResponseDto = new TransactionResponseDto(transactionDtos, statusDtos);
    }

    @Test
    void getTransactions_WithDefaultParameters_ShouldReturnTransactionResponse() throws Exception {
        // Given
        when(transactionService.getTransactionsWithStatus(0, 15)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.status").isArray())
                .andExpect(jsonPath("$.status.length()").value(2))
                
                // Verify first transaction data
                .andExpect(jsonPath("$.data[0].id").value(1372))
                .andExpect(jsonPath("$.data[0].productID").value("10001"))
                .andExpect(jsonPath("$.data[0].productName").value("Test 1"))
                .andExpect(jsonPath("$.data[0].amount").value("1000"))
                .andExpect(jsonPath("$.data[0].customerName").value("abc"))
                .andExpect(jsonPath("$.data[0].status").value(0))
                .andExpect(jsonPath("$.data[0].createBy").value("abc"))
                
                // Verify second transaction data
                .andExpect(jsonPath("$.data[1].id").value(1373))
                .andExpect(jsonPath("$.data[1].productID").value("10002"))
                .andExpect(jsonPath("$.data[1].productName").value("Test 2"))
                .andExpect(jsonPath("$.data[1].amount").value("2000"))
                .andExpect(jsonPath("$.data[1].customerName").value("abc"))
                .andExpect(jsonPath("$.data[1].status").value(1))
                
                // Verify status data
                .andExpect(jsonPath("$.status[0].id").value(0))
                .andExpect(jsonPath("$.status[0].name").value("SUCCESS"))
                .andExpect(jsonPath("$.status[1].id").value(1))
                .andExpect(jsonPath("$.status[1].name").value("FAILED"));

        verify(transactionService, times(1)).getTransactionsWithStatus(0, 15);
    }

    @Test
    void getTransactions_WithCustomParameters_ShouldUseProvidedPageAndSize() throws Exception {
        // Given
        int page = 2;
        int size = 10;
        when(transactionService.getTransactionsWithStatus(page, size)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.status").isArray());

        verify(transactionService, times(1)).getTransactionsWithStatus(page, size);
    }

    @Test
    void getTransactions_WithOnlyPageParameter_ShouldUseDefaultSize() throws Exception {
        // Given
        int page = 1;
        int defaultSize = 15;
        when(transactionService.getTransactionsWithStatus(page, defaultSize)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(transactionService, times(1)).getTransactionsWithStatus(page, defaultSize);
    }

    @Test
    void getTransactions_WithOnlySizeParameter_ShouldUseDefaultPage() throws Exception {
        // Given
        int defaultPage = 0;
        int size = 20;
        when(transactionService.getTransactionsWithStatus(defaultPage, size)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(transactionService, times(1)).getTransactionsWithStatus(defaultPage, size);
    }

    @Test
    void getTransactions_WithEmptyData_ShouldReturnEmptyArrays() throws Exception {
        // Given
        TransactionResponseDto emptyResponse = new TransactionResponseDto(Arrays.asList(), statusDtos);
        when(transactionService.getTransactionsWithStatus(anyInt(), anyInt())).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.status").isArray())
                .andExpect(jsonPath("$.status.length()").value(2));

        verify(transactionService, times(1)).getTransactionsWithStatus(0, 15);
    }

    @Test
    void getTransactions_WithInvalidPageParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("page", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).getTransactionsWithStatus(anyInt(), anyInt());
    }

    @Test
    void getTransactions_WithInvalidSizeParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("size", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).getTransactionsWithStatus(anyInt(), anyInt());
    }

    @Test
    void getTransactions_WithNegativePageParameter_ShouldStillCallService() throws Exception {
        // Given
        int negativePage = -1;
        int defaultSize = 15;
        when(transactionService.getTransactionsWithStatus(negativePage, defaultSize)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("page", String.valueOf(negativePage))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getTransactionsWithStatus(negativePage, defaultSize);
    }

    @Test
    void getTransactions_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(transactionService.getTransactionsWithStatus(anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(transactionService, times(1)).getTransactionsWithStatus(0, 15);
    }

    @Test
    void getTransactions_WithLargePageAndSize_ShouldAcceptParameters() throws Exception {
        // Given
        int largePage = 1000;
        int largeSize = 1000;
        when(transactionService.getTransactionsWithStatus(largePage, largeSize)).thenReturn(transactionResponseDto);

        // When & Then
        mockMvc.perform(get("/api/transactions")
                .param("page", String.valueOf(largePage))
                .param("size", String.valueOf(largeSize))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(transactionService, times(1)).getTransactionsWithStatus(largePage, largeSize);
    }
}
