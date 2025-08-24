package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageError {
    FRANCHISE_ALREADY_EXISTS("Franchise already exists."),
    BRANCH_ALREADY_EXISTS("Branch already exists.");

    private final String message;
}
