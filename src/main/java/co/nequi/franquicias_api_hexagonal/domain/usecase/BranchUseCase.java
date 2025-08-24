package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.exceptions.ErrorMessages;
import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.FranchisePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchUseCase {

    private final BranchPersistencePort branchPersistencePort;
    private final FranchisePersistencePort franchisePersistencePort;

    public Mono<Branch> apply(Branch branch) {
        Branch branchMapped = new Branch(branch.franchiseId(), UUID.randomUUID().toString(), branch.name().trim(), Instant.now());
        return franchisePersistencePort.exists(branch.franchiseId())
                .flatMap(exists -> Boolean.TRUE.equals(exists) ? Mono.empty()
                        : Mono.error(new RuntimeException(ErrorMessages.DATA_NOT_FOUND.getMessage().concat("franchiseId").concat(branch.franchiseId()))))
                .then(Mono.defer(() -> branchPersistencePort.add(branchMapped)));
    }
}
