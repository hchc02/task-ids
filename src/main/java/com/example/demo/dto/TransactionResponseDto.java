package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private List<TransactionDto> data;
    private List<StatusDto> status;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDto {
        private Integer id;
        private String name;
    }
}
