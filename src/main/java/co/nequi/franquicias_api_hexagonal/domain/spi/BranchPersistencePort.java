package co.nequi.franquicias_api_hexagonal.domain.spi;

import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {
    Mono<Branch> add(Branch branch);

    Flux<Branch> listByFranchise(String franchiseId);

    Mono<Branch> findById(String branchId);

    Mono<Branch> updateName(String franchiseId, String branchId, String newName);
}
