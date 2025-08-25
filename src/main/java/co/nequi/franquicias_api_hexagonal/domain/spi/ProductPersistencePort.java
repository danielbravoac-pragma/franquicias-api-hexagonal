package co.nequi.franquicias_api_hexagonal.domain.spi;

import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Mono<Product> add(Product product);

    Mono<Void> delete(String franchiseId, String branchId, String productId);

    Mono<Product> updateStock(String franchiseId, String branchId, String productId, Integer newStock);

    Mono<Product> findTopByBranch(String franchiseId, String branchId);

    Mono<Product> findById(String productId);

    Mono<Product> updateName(String franchiseId, String branchId, String productId, String newName);
}
