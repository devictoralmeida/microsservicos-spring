package com.eazybytes.gatewayserver.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity // Anotação para habilitar a segurança de aplicações Reativas, essa é devido ao spring cloud
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
    serverHttpSecurity.authorizeExchange(exchanges -> exchanges.pathMatchers(HttpMethod.GET).permitAll()
                    // Mesmo se não tiver essa role, se for um metodo GET, ele vai permitir, pois a ordem de configuração importa
                    .pathMatchers("/eazybank/accounts/**").hasRole("ACCOUNTS")
                    .pathMatchers("/eazybank/cards/**").hasRole("CARDS")
                    .pathMatchers("/eazybank/loans/**").hasRole("LOANS"))
            .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                    .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
    serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
    return serverHttpSecurity.build();
  }

  @Bean
  public RouteLocator eazyBankRouteConfig(RouteLocatorBuilder builder) {
    return builder.routes()
            .route(p -> p.path("/eazybank/accounts/**").filters(f -> f.rewritePath("/eazybank/accounts/(?<segment>.*)", "/${segment}"))
//                            .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
//                            .circuitBreaker(config -> config.setName("accountsCircuitBreaker")
//                                    .setFallbackUri("forward:/contactSupport")))
                    .uri("lb://ACCOUNTS"))
            .route(p -> p.path("/eazybank/loans/**").filters(f -> f.rewritePath("/eazybank/loans/(?<segment>.*)", "/${segment}")
//                            .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                            .retry(config -> config.setRetries(3) // Quantidade de tentativas após uma falha
                                    .setMethods(HttpMethod.GET) // Método HTTP permitido
                                    .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)))
                    .uri("lb://LOANS"))
            .route(p -> p.path("/eazybank/cards/**").filters(f -> f.rewritePath("/eazybank/cards/(?<segment>.*)", "/${segment}")
//                            .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                            .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                    .setKeyResolver(userKeyResolver())))
                    .uri("lb://CARDS")).build();
  }

  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
    return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10))
                    .build()).build());
  }

  // O Bean userKeyResolver() é responsável por identificar o usuário que está fazendo a requisição
  @Bean
  KeyResolver userKeyResolver() {
    return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
            .defaultIfEmpty("anonymous");
  }

  // Responsável por limitar a quantidade de requisições que um usuário pode fazer, 1req/s
  @Bean
  public RedisRateLimiter redisRateLimiter() {
    return new RedisRateLimiter(1, 1, 1);
  }

  private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter =
            new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
            (new KeycloakRoleConverter());
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }
}
