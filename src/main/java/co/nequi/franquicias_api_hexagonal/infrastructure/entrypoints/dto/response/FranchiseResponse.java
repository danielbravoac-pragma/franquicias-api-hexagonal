package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Payload for response to an action in Franchise")
public record FranchiseResponse(
        @Schema(description = "Id of Franchise in Database",example = "a1b2-c3d4-e5f6")
        String id,
        @Schema(description = "Franchise name",example = "McDonald's")
        String name,
        @Schema(description = "Date of creation",example = "2025-08-24T01:45:46.719Z")
        Instant createdAt) {
}
