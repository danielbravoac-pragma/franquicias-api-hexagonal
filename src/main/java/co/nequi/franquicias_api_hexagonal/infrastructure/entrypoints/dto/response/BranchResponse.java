package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Payload for response to an action in Franchise")
public record BranchResponse(
        @Schema(description = "Id of Branch in Database", example = "a1b2-c3d4-e5f6")
        String id,
        @Schema(description = "Branch name", example = "McDonald's Colombia")
        String name,
        @Schema(description = "Id of Franchise Associated in Database", example = "a1b2-c3d4-e5f6-fra123")
        String franchiseId,
        @Schema(description = "Date of creation", example = "2025-08-24T01:45:46.719Z")
        Instant createdAt) {
}
