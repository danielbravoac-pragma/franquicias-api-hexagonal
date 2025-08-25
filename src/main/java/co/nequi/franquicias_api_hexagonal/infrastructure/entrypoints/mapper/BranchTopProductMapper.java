package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper;

import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.BranchTopProductResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BranchTopProductMapper {
    BranchTopProductResponse toBranchTopProductResponse(BranchTopProduct branchTopProduct);

    ProductResponse toProductResponse(Product product);
}
