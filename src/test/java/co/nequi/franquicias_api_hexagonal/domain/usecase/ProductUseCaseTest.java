package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.api.ProductServicePort;
import co.nequi.franquicias_api_hexagonal.domain.enums.ErrorMessages;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    ProductPersistencePort productPort;
    @Mock
    BranchPersistencePort branchPort;

    ProductServicePort useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProductUseCase(productPort, branchPort);
    }

    // ---------- add ----------

    @Test
    void add_shouldCreateProductWithTrimmedName_andSetIdsFromBranch() {
        // given
        var fid = "F-1";
        var bid = "B-1";
        var req = new Product(null, bid, null, "  Peluchín  ", 10, null, null);
        var branch = new Branch(fid, bid, "Sucursal Centro", Instant.now());

        when(branchPort.findById(bid)).thenReturn(Mono.just(branch));
        when(productPort.add(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // when
        var result = useCase.add(req);

        // then
        StepVerifier.create(result)
                .assertNext(p -> {
                    assertThat(p.franchiseId()).isEqualTo(fid);
                    assertThat(p.branchId()).isEqualTo(bid);
                    assertThat(p.name()).isEqualTo("Peluchín"); // trimmed
                    assertThat(p.id()).isNotNull().isNotBlank(); // UUID generado
                    assertThat(p.createdAt()).isNotNull();
                    assertThat(p.updatedAt()).isNull(); // como en el use case
                })
                .verifyComplete();

        verify(branchPort).findById(bid);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productPort).add(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.name()).isEqualTo("Peluchín");
        assertThat(saved.franchiseId()).isEqualTo(fid);
        assertThat(saved.branchId()).isEqualTo(bid);
    }

    @Test
    void add_shouldFail_whenBranchNotFound() {
        var bid = "B-404";
        var req = new Product(null, bid, null, "X", 1, null, null);

        when(branchPort.findById(bid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.add(req))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(DataNotFoundException.class);
                    assertThat(err.getMessage()).contains(ErrorMessages.DATA_NOT_FOUND.getMessage(), bid);
                })
                .verify();

        verify(productPort, never()).add(any());
    }

    // ---------- updateStock ----------

    @Test
    void updateStock_shouldCallRepoWithResolvedIds() {
        var fid = "F-1";
        var bid = "B-1";
        var pid = "P-1";
        var branch = new Branch(fid, bid, "Sucursal", Instant.now());
        var req = new Product(null, bid, pid, null, 42, null, null);
        var updated = new Product(fid, bid, pid, "Peluchín", 42, Instant.now(), null);

        when(branchPort.findById(bid)).thenReturn(Mono.just(branch));
        when(productPort.updateStock(fid, bid, pid, 42)).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateStock(req))
                .expectNext(updated)
                .verifyComplete();

        verify(productPort).updateStock(fid, bid, pid, 42);
    }

    @Test
    void updateStock_shouldFail_whenBranchNotFound() {
        var bid = "B-404";
        var req = new Product(null, bid, "P-1", null, 10, null, null);

        when(branchPort.findById(bid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateStock(req))
                .expectError(DataNotFoundException.class)
                .verify();

        verify(productPort, never()).updateStock(any(), any(), any(), anyInt());
    }

    // ---------- delete ----------

    @Test
    void delete_shouldComplete_whenRepoDeletes() {
        var fid = "F-1";
        var bid = "B-1";
        var pid = "P-1";
        var branch = new Branch(fid, bid, "Sucursal", Instant.now());
        var req = new Product(null, bid, pid, null, 0, null, null);

        when(branchPort.findById(bid)).thenReturn(Mono.just(branch));
        when(productPort.delete(fid, bid, pid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(req)).verifyComplete();

        verify(productPort).delete(fid, bid, pid);
    }

    @Test
    void delete_shouldFail_whenBranchNotFound() {
        var req = new Product(null, "B-404", "P-1", null, 0, null, null);
        when(branchPort.findById("B-404")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(req))
                .expectError(DataNotFoundException.class)
                .verify();

        verify(productPort, never()).delete(any(), any(), any());
    }

    // ---------- findTopPerBranchForFranchise ----------

    @Test
    void findTopPerBranchForFranchise_shouldReturnTopOrNullPerBranch() {
        var fid = "F-1";
        var b1 = new Branch(fid, "B-1", "Centro", Instant.now());
        var b2 = new Branch(fid, "B-2", "Norte", Instant.now());
        var pTop = new Product(fid, "B-1", "P-9", "Max", 99, Instant.now(), null);

        when(branchPort.listByFranchise(fid)).thenReturn(Flux.just(b1, b2));
        when(productPort.findTopByBranch(fid, "B-1")).thenReturn(Mono.just(pTop));
        when(productPort.findTopByBranch(fid, "B-2")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findTopPerBranchForFranchise(fid).collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    // No asumimos orden porque el use case usa flatMap
                    var byId = list.stream().collect(java.util.stream.Collectors.toMap(BranchTopProduct::branchId, x -> x));
                    assertThat(byId.get("B-1").product()).isNotNull();
                    assertThat(byId.get("B-1").branchName()).isEqualTo("Centro");
                    assertThat(byId.get("B-2").product()).isNull();
                    assertThat(byId.get("B-2").branchName()).isEqualTo("Norte");
                })
                .verifyComplete();
    }

    // ---------- updateName ----------

    @Test
    void updateName_shouldResolveIdsFromFindById_andUpdate() {
        var fid = "F-1";
        var bid = "B-1";
        var pid = "P-1";
        var found = new Product(fid, bid, pid, "Old", 10, Instant.now(), null);
        var updated = new Product(fid, bid, pid, "New", 10, Instant.now(), null);

        when(productPort.findById(pid)).thenReturn(Mono.just(found));
        when(productPort.updateName(fid, bid, pid, "New")).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateName("New", pid))
                .expectNext(updated)
                .verifyComplete();

        verify(productPort).findById(pid);
        verify(productPort).updateName(fid, bid, pid, "New");
    }

    @Test
    void updateName_shouldFail_whenProductNotFound() {
        var pid = "P-404";
        when(productPort.findById(pid)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateName("X", pid))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(DataNotFoundException.class);
                    assertThat(err.getMessage()).contains(ErrorMessages.DATA_NOT_FOUND.getMessage(), pid);
                })
                .verify();

        verify(productPort, never()).updateName(any(), any(), any(), any());
    }
}
