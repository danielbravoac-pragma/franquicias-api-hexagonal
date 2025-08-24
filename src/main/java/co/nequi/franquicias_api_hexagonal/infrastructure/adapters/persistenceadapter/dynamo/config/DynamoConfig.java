package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class DynamoConfig {
    @Bean
    public DynamoDbAsyncClient dynamo() {
        return DynamoDbAsyncClient.create();
    }
}
