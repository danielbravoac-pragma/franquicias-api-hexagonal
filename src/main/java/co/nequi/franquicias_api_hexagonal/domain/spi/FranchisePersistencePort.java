package co.nequi.franquicias_api_hexagonal.domain.spi;

import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchisePersistencePort {
    Mono<Franchise> create(Franchise franchise);

    Mono<Boolean> exists(String franchiseId);

    Mono<Franchise> findById(String franchiseId);
}
