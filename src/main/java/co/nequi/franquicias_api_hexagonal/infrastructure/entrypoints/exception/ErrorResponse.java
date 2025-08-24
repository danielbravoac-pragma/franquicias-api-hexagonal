package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error Response")
public record ErrorResponse(
        @Schema(description = "Code Error Response")
        String code,
        @Schema(description = "Error Message Response", example = "There's an error")
        String message) {
}
