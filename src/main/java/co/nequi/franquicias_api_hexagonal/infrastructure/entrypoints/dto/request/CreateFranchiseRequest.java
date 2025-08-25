package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Payload for create a Franchise")
public record CreateFranchiseRequest(
        @Schema(description = "Franchise name", example = "McDonald's", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name
) {

}
