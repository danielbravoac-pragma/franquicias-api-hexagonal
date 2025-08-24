package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.repository;

import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.domain.spi.ProductPersistencePort;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.config.DynamoParams;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.DataAlreadyExists;
import co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DynamoProductRepository implements ProductPersistencePort {


    private final DynamoDbAsyncClient dynamo;

    private static String pk(String fid) {
        return "FRANCHISE#" + fid;
    }

    private static String sk(String bid, String pid) {
        return "PRODUCT#" + bid + "#" + pid;
    }

    private static AttributeValue s(String v) {
        return AttributeValue.builder().s(v).build();
    }

    private static AttributeValue n(Integer v) {
        return AttributeValue.builder().n(Integer.toString(v)).build();
    }


    @Override
    public Mono<Product> add(Product product) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(DynamoParams.PK.getValue(), s(pk(product.franchiseId())));
        item.put(DynamoParams.SK.getValue(), s(sk(product.branchId(), product.id())));
        item.put("franchiseId", s(product.franchiseId()));
        item.put("branchId", s(product.branchId()));
        item.put("productId", s(product.id()));
        item.put("name", s(product.name()));
        item.put("stock", n(product.stock()));
        item.put("createdAt", s(product.createdAt().toString()));

        PutItemRequest req = PutItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .item(item)
                .conditionExpression(DynamoParams.ATTRIBUTE_NOT_EXISTS_PK_AND_SK.getValue())
                .build();

        return Mono
                .fromFuture(dynamo.putItem(req))
                .thenReturn(product)
                .onErrorMap(ConditionalCheckFailedException.class,
                        e -> new DataAlreadyExists(MessageError.FRANCHISE_ALREADY_EXISTS.getMessage() + product.id()));
    }

    @Override
    public Mono<Void> delete(String franchiseId, String branchId, String productId) {
        var req = DeleteItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .key(Map.of(DynamoParams.PK.getValue(), s(pk(franchiseId)), DynamoParams.SK.getValue(), s(sk(branchId, productId))))
                .build();
        return Mono.fromFuture(dynamo.deleteItem(req)).then();
    }

    @Override
    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, Integer newStock) {
        var req = UpdateItemRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .key(Map.of(DynamoParams.PK.getValue(), s(pk(franchiseId)), DynamoParams.SK.getValue(), s(sk(branchId, productId))))
                .updateExpression("SET #s = :nv, #ua = :ts")
                .expressionAttributeNames(Map.of("#s", "stock","#ua","updatedAt"))
                .expressionAttributeValues(Map.of(":nv", n(newStock),":ts",s(Instant.now().toString())))
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        return Mono.fromFuture(dynamo.updateItem(req))
                .map(r -> {
                    var i = r.attributes();
                    return new Product(
                            franchiseId, branchId,
                            i.getOrDefault("productId", s(productId)).s(),
                            i.getOrDefault("name", s("")).s(),
                            Integer.parseInt(i.get("stock").n()),
                            Instant.parse(i.get("createdAt").s()),
                            Instant.parse(i.get("updatedAt").s())
                    );
                });
    }

    @Override
    public Mono<Product> findTopByBranch(String franchiseId, String branchId) {
        var q = QueryRequest.builder()
                .tableName(DynamoParams.TABLE_NAME.getValue())
                .indexName(DynamoParams.BRANCH_PRODUCTS_BY_STOCK_INDEX.getValue())
                .keyConditionExpression("branchId = :b")
                .expressionAttributeValues(Map.of(":b", s(branchId)))
                .scanIndexForward(false).limit(1)
                .build();

        return Mono.fromFuture(dynamo.query(q))
                .flatMap(resp -> Mono.justOrEmpty(resp.items().stream().findFirst()))
                .map(i -> new Product(
                        franchiseId, branchId,
                        i.get("productId").s(),
                        i.get("name").s(),
                        Integer.parseInt(i.get("stock").n()),
                        Instant.parse(i.get("createdAt").s()),
                        Instant.parse(i.getOrDefault("updatedAt",s(Instant.now().toString())).s())
                ));
    }
}
