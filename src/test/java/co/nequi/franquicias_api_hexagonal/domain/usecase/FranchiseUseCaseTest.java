package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.domain.enums.ErrorMessages;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import co.nequi.franquicias_api_hexagonal.domain.spi.FranchisePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    FranchisePersistencePort port;
    FranchiseServicePort useCase;

    @BeforeEach
    void setUp() {
        useCase = new FranchiseUseCase(port);
    }

    // -------- apply (crear) --------

    @Test
    void apply_shouldCreateWithTrimmedName_andGenerateUuid_andCreatedAt() {
        // given
        var inputName = "Mi Franquicia";
        when(port.create(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // when
        var result = useCase.apply(inputName);

        // then
        StepVerifier.create(result)
                .assertNext(f -> {
                    // nombre recortado
                    assertThat(f.name()).isEqualTo("Mi Franquicia");
                    // id UUID válido
                    UUID.fromString(f.id());
                    // createdAt no nulo (no validamos valor exacto)
                    assertThat(f.createdAt()).isNotNull();
                })
                .verifyComplete();

        // Verifica lo que se envió al puerto
        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(port).create(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.name()).isEqualTo("Mi Franquicia");
        UUID.fromString(saved.id());
        assertThat(saved.createdAt()).isNotNull();
    }

    // -------- updateName --------

    @Test
    void updateName_shouldFindAndDelegateToRepo() {
        // given
        var fid = "F-1";
        var existing = new Franchise(fid, "Old", Instant.now());
        var updated = new Franchise(fid, "New", existing.createdAt());

        when(port.findById(fid)).thenReturn(Mono.just(existing));
        when(port.updateName(fid, "New")).thenReturn(Mono.just(updated));

        // when / then
        StepVerifier.create(useCase.updateName(fid, "New"))
                .expectNext(updated)
                .verifyComplete();

        verify(port).findById(fid);
        verify(port).updateName(fid, "New");
    }

    @Test
    void updateName_shouldFail_whenFranchiseNotFound() {
        // given
        var fid = "F-404";
        when(port.findById(fid)).thenReturn(Mono.empty());

        // when / then
        StepVerifier.create(useCase.updateName(fid, "X"))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(DataNotFoundException.class);
                    assertThat(err.getMessage()).contains(ErrorMessages.DATA_NOT_FOUND.getMessage(), fid);
                })
                .verify();

        verify(port, never()).updateName(anyString(), anyString());
    }
}
