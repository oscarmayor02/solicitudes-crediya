package co.com.pragma.solicitudes.r2dbc;

import co.com.pragma.solicitudes.model.loantype.LoanType;
import co.com.pragma.solicitudes.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.solicitudes.r2dbc.mapper.LoanTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class loanTypeRepositoryAdapterTest {

    private ILoanTypeReactiveRepository reactiveRepository;
    private LoanTypeMapper mapper;
    private LoanTypeRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        reactiveRepository = Mockito.mock(ILoanTypeReactiveRepository.class);
        mapper = Mockito.mock(LoanTypeMapper.class);
        adapter = new LoanTypeRepositoryAdapter(reactiveRepository, mapper);
    }

    @Test
    void saveTipoPrestamo_Exitoso() {
        LoanType tipo = new LoanType();
        when(mapper.toEntity(any())).thenReturn(new LoanTypeEntity());
        when(mapper.toModel(any())).thenReturn(tipo);
        when(reactiveRepository.save(any())).thenReturn(Mono.just(new LoanTypeEntity()));

        StepVerifier.create(adapter.save(tipo))
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void findAllTiposPrestamo_Exitoso() {
        LoanType tipo = new LoanType();
        when(reactiveRepository.findAll()).thenReturn(Flux.just(new LoanTypeEntity()));
        when(mapper.toModel(any())).thenReturn(tipo);

        StepVerifier.create(adapter.findAll())
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void findById_Exitoso() {
        LoanType tipo = new LoanType();
        when(reactiveRepository.findById(1L)).thenReturn(Mono.just(new LoanTypeEntity()));
        when(mapper.toModel(any())).thenReturn(tipo);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(tipo)
                .verifyComplete();
    }

    @Test
    void existsByNombreIgnoreCase_Exitoso() {
        when(reactiveRepository.existsByNameIgnoreCase("Personal")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameIgnoreCase("Personal"))
                .expectNext(true)
                .verifyComplete();
    }
}
