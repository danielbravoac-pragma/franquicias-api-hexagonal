package co.nequi.franquicias_api_hexagonal.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessages {

    DATA_NOT_FOUND("Id not found in database: ");

    private final String message;
}
