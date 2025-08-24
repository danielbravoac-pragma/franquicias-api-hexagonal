package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T t) {
        if (t == null)
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST.getMessage()));

        Set<ConstraintViolation<T>> constraints = validator.validate(t);

        if (constraints.isEmpty()) return Mono.just(t);

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST.getMessage()));
    }
}
