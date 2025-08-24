package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository;

import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.domain.spi.BranchPersistencePort;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.config.DynamoParams;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.DataAlreadyExists;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DynamoBranchRepository implements BranchPersistencePort {

    private final DynamoDbAsyncClient dynamo;

    private static String pkValue(String fid) {
        return "FRANCHISE#" + fid;
    }

    private static String skValue(String bid) {
        return "BRANCH#" + bid;
    }

    private static AttributeValue s(String v) {
        return AttributeValue.builder().s(v).build();
    }


    @Override
    public Mono<Branch> add(Branch branch) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(DynamoParams.PK.getValue(), s(pkValue(branch.franchiseId())));
        item.put(DynamoParams.SK.getValue(), s(skValue(branch.id())));
        item.put("franchiseId", s(branch.franchiseId()));
        item.put("branchId", s(branch.id()));
        item.put("name", s(branch.name()));
        item.put("createdAt", s(branch.createdAt().toString()));

        PutItemRequest req = PutItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .item(item)
                .conditionExpression(DynamoParams.ATTRIBUTE_NOT_EXISTS_PK_AND_SK.getValue())
                .build();
        return Mono
                .fromFuture(dynamo.putItem(req))
                .thenReturn(branch)
                .onErrorMap(ConditionalCheckFailedException.class,
                        e -> new DataAlreadyExists(MessageError.BRANCH_ALREADY_EXISTS.getMessage() + branch.id()));
    }

    @Override
    public Flux<Branch> listByFranchise(String franchiseId) {
        QueryRequest req = QueryRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .keyConditionExpression("pk = :pk AND begins_with(sk, :pref)")
                .expressionAttributeValues(Map.of(
                        ":pk", s(pkValue(franchiseId)),
                        ":pref", s("BRANCH#")
                ))
                .build();

        SdkPublisher<QueryResponse> pages = dynamo.queryPaginator(req);
        return Flux.from(pages)
                .flatMapIterable(QueryResponse::items)
                .map(i -> new Branch(
                        i.get("franchiseId").s(),
                        i.get("branchId").s(),
                        i.get("name").s(),
                        Instant.parse(i.get("createdAt").s())
                ));
    }

    @Override
    public Mono<Branch> findById(String branchId) {
        QueryRequest q = QueryRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue()) // o props.tableName()
                .indexName(DynamoParams.GSI_BRANCH_BY_ID_INDEX.getValue())
                .keyConditionExpression("branchId = :b")
                .expressionAttributeValues(Map.of(":b", s(branchId)))
                .limit(1)
                .build();
        return Mono.fromFuture(dynamo.query(q))
                .flatMap(resp -> Mono.justOrEmpty(resp.items().stream().findFirst()))
                .map(i -> new Branch(
                        i.get("franchiseId").s(),
                        i.get("branchId").s(),
                        i.get("name").s(),
                        Instant.parse(i.get("createdAt").s())
                ));
    }

}
