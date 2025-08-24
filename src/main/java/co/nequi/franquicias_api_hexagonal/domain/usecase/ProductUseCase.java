package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.api.ProductServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.enums.ErrorMessages;
import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductUseCase implements ProductServicePort {

    private final ProductPersistencePort productPersistencePort;
    private final BranchPersistencePort branchPersistencePort;

    @Override
    public Mono<Product> add(Product product) {
        return branchPersistencePort.findById(product.branchId())
                .switchIfEmpty(Mono.error(
                        new DataNotFoundException(ErrorMessages.DATA_NOT_FOUND.getMessage().concat(product.branchId())))
                )
                .flatMap(b -> {
                    Product product1 = new Product(
                            b.franchiseId(),
                            b.id(),
                            UUID.randomUUID().toString(),
                            product.name().trim(),
                            product.stock(),
                            Instant.now(),
                            null);
                    return productPersistencePort.add(product1);
                });
    }

    @Override
    public Mono<Product> updateStock(Product product) {
        return branchPersistencePort.findById(product.branchId())
                .switchIfEmpty(Mono.error(
                        new DataNotFoundException(ErrorMessages.DATA_NOT_FOUND.getMessage().concat(product.branchId())))
                )
                .flatMap(b -> productPersistencePort.updateStock(
                        b.franchiseId(),
                        b.id(),
                        product.id(),
                        product.stock()));
    }

    @Override
    public Mono<Void> delete(Product product) {
        return branchPersistencePort.findById(product.branchId())
                .switchIfEmpty(Mono.error(
                        new DataNotFoundException(ErrorMessages.DATA_NOT_FOUND.getMessage().concat(product.branchId())))
                )
                .flatMap(b -> productPersistencePort.delete(
                        b.franchiseId(),
                        b.id(),
                        product.id()
                ));
    }

    @Override
    public Flux<BranchTopProduct> findTopPerBranchForFranchise(String franchiseId) {
        return branchPersistencePort.listByFranchise(franchiseId)
                .flatMap(b ->
                        productPersistencePort.findTopByBranch(franchiseId, b.id())
                                .map(p -> new BranchTopProduct(b.id(), b.name(), p))
                                .switchIfEmpty(Mono.just(new BranchTopProduct(b.id(), b.name(), null)))
                );
    }

    @Override
    public Mono<Product> updateName(String name, String productId) {
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(new DataNotFoundException(ErrorMessages.DATA_NOT_FOUND.getMessage().concat(productId))))
                .flatMap(p -> productPersistencePort.updateName(p.franchiseId(), p.branchId(), p.id(), name));
    }
}
