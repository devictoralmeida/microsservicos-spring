package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseTraceFilter {
  private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

  @Autowired
  FilterUtility filterUtility;

//  @Bean
//  public GlobalFilter postGlobalFilter() {
//    return (exchange, chain) -> {
//      return chain.filter(exchange).then(Mono.fromRunnable(() -> { // Indicando que a execução do filtro ocorrerá após receber a resposta do MS
//        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
//        String correlationId = filterUtility.getCorrelationId(requestHeaders);
////        exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
//        if (!(exchange.getResponse().getHeaders().containsKey(filterUtility.CORRELATION_ID))) {
//          logger.debug("Updated the correlation id to the outbound headers: {}", correlationId);
//          exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
//        }
//      }));
//    };
//  }
}
