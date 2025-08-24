package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for update Franchise Name")
public record UpdateFranchiseNameRequest(
        @Schema(description = "Franchise Id to update", example = "a1b2c3-d4e5f6-g7h8i9", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String franchiseId,
        @Schema(description = "New Franchise name", example = "McDonald's The New Eve", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name
) {
}
