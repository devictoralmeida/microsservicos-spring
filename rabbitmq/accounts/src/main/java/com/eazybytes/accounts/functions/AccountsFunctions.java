package com.eazybytes.accounts.functions;

import com.eazybytes.accounts.service.IAccountsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFunctions {
  private static final Logger log = LoggerFactory.getLogger(AccountsFunctions.class);

  // Implementação da função Consumer<T>
  @Bean // Devido ao @Bean, o parâmetro dessa função será injetada automaticamente
  public Consumer<Long> updateCommunication(IAccountsService accountsService) {
    return accountNumber -> { // aqui é o input que é o retorno do método sms() da função MessageFunctions
      log.info("Atualizando o status da mensagem para o número da conta: " + accountNumber.toString());
      accountsService.updateCommunicationStatus(accountNumber);
    };
  }
}
