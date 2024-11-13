package com.eazybytes.accounts.service.client;

import com.eazybytes.accounts.dto.LoansDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoansFallback implements LoansFeignClient {
  @Override
  public ResponseEntity<LoansDto> fetchLoanDetails(String correlationId, String mobileNumber) {
    return null; // Retorna null para evitar uma RuntimeException, aqui poderia ser retornando um valor padrão armazenado em cache
  }
}
