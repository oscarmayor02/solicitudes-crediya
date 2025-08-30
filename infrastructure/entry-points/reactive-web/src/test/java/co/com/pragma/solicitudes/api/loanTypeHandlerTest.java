package co.com.pragma.solicitudes.api;

import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.usecase.loanType.LoanTypeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class loanTypeHandlerTest {

    private LoanTypeUseCase useCase; // Mock del caso de uso
    private LoanTypeHandler handler; // Handler a testear
    private WebTestClient client; // Cliente WebTestClient

    @BeforeEach
    void setup() {
        useCase = Mockito.mock(LoanTypeUseCase.class); // Creamos mock
        handler = new LoanTypeHandler(useCase); // Inyectamos mock en handler
        client = WebTestClient.bindToRouterFunction(
                new RouterRestLoanType().loanTypeRoutes(handler)).build();
        // Cliente ligado al router de LoanType
    }

    @Test
    void crearTipoPrestamo_Exitoso() {
        LoanType tipo = new LoanType();
        tipo.setName("Personal");
        tipo.setMinimumAmount(BigDecimal.valueOf(1000));
        tipo.setMaximumAmount(BigDecimal.valueOf(5000));
        tipo.setRateInterest(BigDecimal.valueOf(2)); // Valores de prueba

        when(useCase.create(any())).thenReturn(Mono.just(tipo));
        // Mock: al crear, devuelve el tipo prestamo

        client.post() // POST
                .uri("/api/v1/tipos-prestamo") // Endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tipo) // Body JSON
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoanType.class)
                .isEqualTo(tipo); // Compara respuesta con mock
    }

    @Test
    void listarTiposPrestamo_Exitoso() {
        LoanType t1 = new LoanType();
        LoanType t2 = new LoanType();
        when(useCase.listLoanType()).thenReturn(Flux.just(t1, t2));
        // Mock: lista de tipos de prestamo

        client.get()
                .uri("/api/v1/tipos-prestamo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LoanType.class)
                .hasSize(2); // Verifica que la lista tenga 2 elementos
    }
}
