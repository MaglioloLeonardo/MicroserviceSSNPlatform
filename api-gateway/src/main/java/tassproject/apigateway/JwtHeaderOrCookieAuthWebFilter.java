package tassproject.apigateway;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderOrCookieAuthWebFilter implements WebFilter {

    private final ReactiveJwtDecoder jwtDecoder;
    private final ReactiveJwtAuthenticationConverterAdapter converter;

    public JwtHeaderOrCookieAuthWebFilter(
            ReactiveJwtDecoder jwtDecoder,
            ReactiveJwtAuthenticationConverterAdapter converter
    ) {
        this.jwtDecoder = jwtDecoder;
        this.converter  = converter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (cookie != null) {
            token = cookie.getValue();
        }
        if (token == null) {
            return chain.filter(exchange);
        }

        return jwtDecoder.decode(token)
                .flatMap(jwt -> converter.convert(jwt))
                .flatMap(auth ->
                        chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                )
                .onErrorResume(e -> unauthorized(exchange));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
