package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository;

import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import co.nequi.franquicias_api_hexagonal.domain.spi.FranchisePersistencePort;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.config.DynamoParams;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.DataAlreadyExists;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DynamoFranchiseRepository implements FranchisePersistencePort {

    private final DynamoDbAsyncClient dynamo;

    private static String pkValue(String fid) {
        return "FRANCHISE#" + fid;
    }

    private static String skValue() {
        return "METADATA";
    }

    private static AttributeValue s(String v) {
        return AttributeValue.builder().s(v).build();
    }

    @Override
    public Mono<Franchise> create(Franchise franchise) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(DynamoParams.PK.getValue(), s(pkValue(franchise.id())));
        item.put(DynamoParams.SK.getValue(), s(skValue()));
        item.put("franchiseId", s(franchise.id()));
        item.put("name", s(franchise.name()));
        item.put("createdAt", s(franchise.createdAt().toString()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .item(item)
                .conditionExpression(DynamoParams.ATTRIBUTE_NOT_EXISTS_PK_AND_SK.getValue())
                .build();

        return Mono
                .fromFuture(dynamo.putItem(request))
                .thenReturn(franchise)
                .onErrorMap(ConditionalCheckFailedException.class,
                        e -> new DataAlreadyExists(MessageError.FRANCHISE_ALREADY_EXISTS.getMessage() + franchise.id()));
    }

    @Override
    public Mono<Boolean> exists(String franchiseId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .key(Map.of(DynamoParams.PK.getValue(), s(pkValue(franchiseId)), DynamoParams.SK.getValue(), s(skValue())))
                .build();

        return Mono
                .fromFuture(dynamo.getItem(request))
                .map(r -> r.item() != null && !r.item().isEmpty());

    }

    @Override
    public Mono<Franchise> findById(String franchiseId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .key(Map.of(DynamoParams.PK.getValue(), s(pkValue(franchiseId)), DynamoParams.SK.getValue(), s(skValue())))
                .build();

        return Mono.fromFuture(dynamo.getItem(request))
                .flatMap(e -> {
                    var i = e.item();
                    if (i == null || i.isEmpty()) return Mono.empty();
                    return Mono.just(new Franchise(
                            i.get("franchiseId").s(),
                            i.get("name").s(),
                            Instant.parse(i.get("createdAt").s())));
                });
    }
}
