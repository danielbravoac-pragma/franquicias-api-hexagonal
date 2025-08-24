package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper;

import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.DeleteProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateStockRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);

    Product toProduct(CreateProductRequest createProductRequest);

    @Mapping(source = "productId", target = "id")
    Product toProductFromUpdateStock(UpdateStockRequest updateStockRequest);

    @Mapping(source = "productId", target = "id")
    Product toProductFromDeleteProduct(DeleteProductRequest deleteProductRequest);
}
