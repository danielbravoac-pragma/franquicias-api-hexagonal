package co.nequi.franquicias_api_hexagonal.application.config;

import co.nequi.franquicias_api_hexagonal.domain.api.BranchServicePort;
import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.domain.api.ProductServicePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.FranchisePersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.spi.ProductPersistencePort;
import co.nequi.franquicias_api_hexagonal.domain.usecase.BranchUseCase;
import co.nequi.franquicias_api_hexagonal.domain.usecase.FranchiseUseCase;
import co.nequi.franquicias_api_hexagonal.domain.usecase.ProductUseCase;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository.DynamoBranchRepository;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository.DynamoFranchiseRepository;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository.DynamoProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    @Bean
    public FranchisePersistencePort franchisePersistencePort() {
        return new DynamoFranchiseRepository(dynamoDbAsyncClient);
    }

    @Bean
    public FranchiseServicePort franchiseServicePort() {
        return new FranchiseUseCase(franchisePersistencePort());
    }

    @Bean
    public BranchPersistencePort branchPersistencePort() {
        return new DynamoBranchRepository(dynamoDbAsyncClient);
    }

    @Bean
    public BranchServicePort branchServicePort() {
        return new BranchUseCase(branchPersistencePort(), franchisePersistencePort());
    }

    @Bean
    public ProductPersistencePort productPersistencePort() {
        return new DynamoProductRepository(dynamoDbAsyncClient);
    }

    @Bean
    public ProductServicePort productServicePort() {
        return new ProductUseCase(productPersistencePort(), branchPersistencePort());
    }


}
