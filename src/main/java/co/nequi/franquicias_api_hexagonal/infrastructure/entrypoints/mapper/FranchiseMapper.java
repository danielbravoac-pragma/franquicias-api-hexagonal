package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper;

import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.FranchiseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FranchiseMapper {
    FranchiseResponse toFranchiseResponse(Franchise franchise);
}
