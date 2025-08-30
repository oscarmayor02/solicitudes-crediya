package co.com.pragma.solicitudes.r2dbc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${clients.autenticacion.base-url}")
    private String authBaseUrl;
    @Bean
    public WebClient autenticacionClient() {
        return WebClient.builder().baseUrl(authBaseUrl).build();
    }
}