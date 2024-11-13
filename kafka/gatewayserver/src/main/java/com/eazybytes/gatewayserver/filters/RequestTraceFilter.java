package com.eazybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

// @Order(1) // Indicando a ordem de execução do filtro
@Component // Indicando que é um componente do Spring
//public class RequestTraceFilter implements GlobalFilter {
public class RequestTraceFilter {
  private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

  @Autowired
  FilterUtility filterUtility;

//  @Override
//  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
//    if (isCorrelationIdPresent(requestHeaders)) {
//      logger.debug("eazyBank-correlation-id found in RequestTraceFilter : {}",
//              filterUtility.getCorrelationId(requestHeaders));
//    } else {
//      String correlationID = generateCorrelationId();
//      ServerWebExchange mutatedExchange = filterUtility.setCorrelationId(exchange, correlationID);
//      logger.debug("eazyBank-correlation-id generated in RequestTraceFilter : {}", correlationID);
//      return chain.filter(mutatedExchange);
//    }
//    return chain.filter(exchange);
//  }

  private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
    return filterUtility.getCorrelationId(requestHeaders) != null;
  }

  private String generateCorrelationId() {
    return java.util.UUID.randomUUID().toString();
  }
}
