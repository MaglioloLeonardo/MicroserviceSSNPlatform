package tassproject.apigateway;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Questo filtro intercetta la richiesta:
 * - Se manca l'header Authorization ma esiste il cookie access_token, aggiunge l'header.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtCookieAuthWebFilter implements WebFilter {

    private static final String COOKIE_NAME = "access_token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Se già presente Authorization, passa avanti
        if (authHeader != null && !authHeader.isBlank()) {
            return chain.filter(exchange);
        }

        // Altrimenti cerca il cookie
        return Mono.defer(() -> {
            var cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);
            if (cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                // Crea una nuova request con l'header Authorization: Bearer <cookie_value>
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue())
                        .build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            }
            // Nessun JWT in header o cookie → passa avanti senza mutare nulla (scatta la security dopo)
            return chain.filter(exchange);
        });
    }
}
