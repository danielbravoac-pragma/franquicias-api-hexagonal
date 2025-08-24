package co.nequi.franquicias_api_hexagonal.domain.api;

import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    public Mono<Product> add(Product product);

    public Mono<Product> updateStock(Product product);

    public Mono<Void> delete(Product product);

    public Flux<BranchTopProduct> findTopPerBranchForFranchise(String franchiseId);
}
