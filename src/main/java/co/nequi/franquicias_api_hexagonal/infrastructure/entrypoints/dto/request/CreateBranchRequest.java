package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for create a Branch")
public record CreateBranchRequest(
        @Schema(description = "Franchise id associated to new Branch", example = "a1b2c3-d4e5f6-g7h8i9", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String franchiseId,

        @Schema(description = "Branch name", example = "McDonald's Colombia", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name
) {
}
