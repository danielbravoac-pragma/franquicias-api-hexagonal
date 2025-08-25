package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for Top of Products by Branch")
public record BranchTopProductResponse(
        @Schema(description = "Id of Branch in Database", example = "a1b2-c3d4-e5f6")
        String branchId,
        @Schema(description = "Branch name", example = "McDonald's Colombia")
        String branchName,
        @Schema(description = "Product Response for Branch Top")
        ProductResponse product
) {
}
