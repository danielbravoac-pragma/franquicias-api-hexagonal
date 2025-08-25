package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessage {

    BAD_REQUEST("Bad Request, check your request parameters."),
    NOT_FOUND("Resource not found."),
    INTERNAL_SERVER_ERROR("Internal Server Error, please contact the administrator."),
    FORBIDDEN("Forbidden, you don't have permission to access this resource."),
    ENTITY_ASSOCIATED_DOES_NOT_EXIST("You're trying to associate the record with a non existent entity");

    private final String message;
}
