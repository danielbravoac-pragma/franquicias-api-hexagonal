package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper;

import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.BranchResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateBranchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BranchMapper {
    BranchResponse toBranchResponse(Branch branch);

    Branch toBranch(CreateBranchRequest createBranchRequest);
}
