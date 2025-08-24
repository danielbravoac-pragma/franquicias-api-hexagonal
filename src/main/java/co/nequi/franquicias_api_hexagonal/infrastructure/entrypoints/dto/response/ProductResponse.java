package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Payload for response to an action in Product")
public record ProductResponse(
        @Schema(description = "Id of Product in Database", example = "a1b2-c3d4-e5f6")
        String id,
        @Schema(description = "Product name", example = "BigMac Gold x Strikers")
        String name,
        @Schema(description = "Stock of Product", example = "890")
        Integer stock,
        @Schema(description = "Id of Franchise Associated in Database", example = "a1b2-c3d4-e5f6-fra123")
        String franchiseId,
        @Schema(description = "Id of Branch Associated in Database", example = "a1b2-c3d4-e5f6-bra123")
        String branchId,
        @Schema(description = "Date of creation", example = "2025-08-24T01:45:46.719Z")
        Instant createdAt,
        @Schema(description = "Date of update", example = "2025-08-24T01:45:46.719Z")
        Instant updatedAt
) {
}
