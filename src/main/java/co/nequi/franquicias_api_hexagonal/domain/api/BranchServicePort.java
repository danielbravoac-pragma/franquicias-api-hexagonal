package co.nequi.franquicias_api_hexagonal.domain.api;

import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<Branch> apply(Branch branch);

    Mono<Branch> updateName(String branchId, String newName);
}
