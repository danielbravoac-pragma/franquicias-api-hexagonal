package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DynamoParams {
    TABLE_NAME("FranchisesNetwork"),
    PK("pk"),
    SK("sk"),
    ATTRIBUTE_NOT_EXISTS_PK_AND_SK("attribute_not_exists(pk) AND attribute_not_exists(sk)"),
    ATTRIBUTE_EXISTS_PK_AND_SK("attribute_exists(pk) AND attribute_exists(sk)"),
    GSI_BRANCH_BY_ID_INDEX("GSI_BranchById"),
    BRANCH_PRODUCTS_BY_STOCK_INDEX("GSI_BranchProductsByStock"),
    PRODUCT_BY_ID_INDEX("GSI_ProductById");

    private final String value;
}
