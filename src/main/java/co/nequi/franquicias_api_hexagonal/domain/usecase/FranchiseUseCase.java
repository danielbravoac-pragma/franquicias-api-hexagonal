package co.nequi.franquicias_api_hexagonal.domain.usecase;

import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import co.nequi.franquicias_api_hexagonal.domain.spi.FranchisePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;

    @Override
    public Mono<Franchise> apply(String name) {
        Franchise franchise = new Franchise(UUID.randomUUID().toString(), name.trim(), Instant.now());
        return franchisePersistencePort.create(franchise);
    }
}
