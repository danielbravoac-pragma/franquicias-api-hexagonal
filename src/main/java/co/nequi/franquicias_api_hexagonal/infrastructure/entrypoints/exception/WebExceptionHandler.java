package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext,
                               ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest) {
        Map<String, Object> generalError = getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults());

        Throwable ex = getError(serverRequest);

        int statusCode = Integer.valueOf(String.valueOf(generalError.get("status")));

        ErrorResponse errorResponse;

        switch (statusCode) {
            case 400, 422 ->
                    errorResponse = new ErrorResponse(String.valueOf(statusCode), ErrorMessage.BAD_REQUEST.getMessage());
            case 403 ->
                    errorResponse = new ErrorResponse(String.valueOf(statusCode), ErrorMessage.FORBIDDEN.getMessage());
            case 404 ->
                    errorResponse = new ErrorResponse(String.valueOf(statusCode), ErrorMessage.NOT_FOUND.getMessage());
            case 500 ->
                    errorResponse = new ErrorResponse(String.valueOf(statusCode), ErrorMessage.INTERNAL_SERVER_ERROR.getMessage());

            default -> errorResponse = new ErrorResponse(String.valueOf(statusCode), ex.getMessage());
        }

        return ServerResponse.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

}
