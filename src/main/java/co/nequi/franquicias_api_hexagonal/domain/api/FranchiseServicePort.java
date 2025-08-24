package co.nequi.franquicias_api_hexagonal.domain.api;

import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> apply(String name);

    Mono<Franchise> updateName(String franchiseId, String newName);
}
