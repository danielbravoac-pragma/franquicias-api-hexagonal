package co.nequi.franquicias_api_hexagonal.domain.api;

import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    Mono<Product> add(Product product);

    Mono<Product> updateStock(Product product);

    Mono<Void> delete(Product product);

    Flux<BranchTopProduct> findTopPerBranchForFranchise(String franchiseId);

    Mono<Product> updateName(String name, String productId);

}
