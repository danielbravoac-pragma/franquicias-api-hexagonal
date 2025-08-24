package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for update Product Name")
public record UpdateProductNameRequest(
        @Schema(description = "Product Id to update", example = "a1b2c3-d4e5f6-g7h8i9", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String productId,
        @Schema(description = "New Product name",example = "New Big Mac With Renovated Salsa",requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name) {
}
