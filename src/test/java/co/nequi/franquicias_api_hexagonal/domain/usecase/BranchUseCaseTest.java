package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.api.BranchServicePort;
import co.nequi.franquicias_api_hexagonal.domain.enums.ErrorMessages;
import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
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
class BranchUseCaseTest {

    @Mock
    BranchPersistencePort branchPort;
    @Mock
    FranchisePersistencePort franchisePort;

    BranchServicePort useCase;

    @BeforeEach
    void setUp() {
        useCase = new BranchUseCase(branchPort, franchisePort);
    }

    // ---------- apply (crear sucursal) ----------

    @Test
    void apply_shouldCreate_whenFranchiseExists_trimName_andGenerateIdAndCreatedAt() {
        // given
        var fid = "F-1";
        var req = new Branch(fid, null, "  Sucursal Centro  ", null);

        // la franquicia existe
        when(franchisePort.exists(fid)).thenReturn(Mono.just(true));
        // que el repo retorne lo mismo que recibe, para poder asertar el mapeo del use case
        when(branchPort.add(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // when
        var result = useCase.apply(req);

        // then
        StepVerifier.create(result)
                .assertNext(b -> {
                    assertThat(b.franchiseId()).isEqualTo(fid);
                    assertThat(b.name()).isEqualTo("Sucursal Centro"); // trimmed
                    assertThat(b.createdAt()).isNotNull();
                    // id generado como UUID
                    UUID.fromString(b.id());
                })
                .verifyComplete();

        // capturamos lo que se envió al repo
        ArgumentCaptor<Branch> cap = ArgumentCaptor.forClass(Branch.class);
        verify(branchPort).add(cap.capture());
        var saved = cap.getValue();
        assertThat(saved.franchiseId()).isEqualTo(fid);
        assertThat(saved.name()).isEqualTo("Sucursal Centro");
        assertThat(saved.createdAt()).isNotNull();
        UUID.fromString(saved.id());

        verify(franchisePort).exists(fid);
    }

    @Test
    void apply_shouldFail_whenFranchiseDoesNotExist() {
        var fid = "F-404";
        var req = new Branch(fid, null, "X", null);

        when(franchisePort.exists(fid)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.apply(req))
                .expectErrorSatisfies(err -> {
                    // la implementación actual lanza RuntimeException con el mensaje DATA_NOT_FOUND + "franchiseId" + <id>
                    assertThat(err).isInstanceOf(RuntimeException.class);
                    assertThat(err.getMessage()).contains(
                            ErrorMessages.DATA_NOT_FOUND.getMessage(), "franchiseId", fid);
                })
                .verify();

        verify(branchPort, never()).add(any());
    }

    // ---------- updateName (solo branchId) ----------

    @Test
    void updateName_shouldFindById_andDelegateToRepo() {
        var fid = "F-1";
        var bid = "B-1";
        var existing = new Branch(fid, bid, "Old", Instant.now());
        var updated = new Branch(fid, bid, "New", existing.createdAt());

        when(branchPort.findById(bid)).thenReturn(Mono.just(existing));
        when(branchPort.updateName(fid, bid, "New")).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateName(bid, "New"))
                .expectNext(updated)
                .verifyComplete();

        verify(branchPort).findById(bid);
        verify(branchPort).updateName(fid, bid, "New");
    }

    @Test
    void updateName_shouldFail_whenBranchNotFound() {
        var bid = "B-404";
        when(branchPort.findById(bid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateName(bid, "Nuevo"))
                .expectErrorSatisfies(err -> {
                    // la implementación actual lanza RuntimeException con DATA_NOT_FOUND + <branchId>
                    assertThat(err).isInstanceOf(RuntimeException.class);
                    assertThat(err.getMessage()).contains(
                            ErrorMessages.DATA_NOT_FOUND.getMessage(), bid);
                })
                .verify();

        verify(branchPort, never()).updateName(any(), any(), any());
    }
}
