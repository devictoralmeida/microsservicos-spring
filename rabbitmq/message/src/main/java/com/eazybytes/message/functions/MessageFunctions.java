package com.eazybytes.message.functions;

import com.eazybytes.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {
  private static final Logger log = LoggerFactory.getLogger(MessageFunctions.class);

  @Bean
  // Function<intput, output>
  public Function<AccountsMsgDto, AccountsMsgDto> email() {
    return accountsMsgDto -> { // aqui é o input
      log.info("Enviando e-mail com os seguintes detalhes: " + accountsMsgDto.toString());
      return accountsMsgDto; // aqui é o output
    };
    // Essa função irá retornar um AccountsMsgDto que será passado para a próxima função
  }

  @Bean
  public Function<AccountsMsgDto, Long> sms() {
    return accountsMsgDto -> { // aqui é o input
      log.info("Enviando SMS com os seguintes detalhes: " + accountsMsgDto.toString());
      return accountsMsgDto.accountNumber(); // aqui é o output
    };
  }
}
