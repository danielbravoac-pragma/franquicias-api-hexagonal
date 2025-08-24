package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Payload for create a Product")
public record CreateProductRequest(
        @Schema(description = "Product name", example = "BigMac Gold x Strikers", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String name,
        @Schema(description = "Branch Id associated to new Product", example = "a1b2c3-d4e5f6-g7h8i9", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String branchId,
        @Schema(description = "Initial stock for Product", example = "890", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @PositiveOrZero
        Integer stock
) {
}
