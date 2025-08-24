package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for update Branch Name")
public record UpdateBranchNameRequest(
        @Schema(description = "Branch Id to update", example = "a1b2c3-d4e5f6-g7h8i9", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String branchId,
        @Schema(description = "New Branch name",example = "McDonald's The New Eve Colombia",requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name
) {
}
