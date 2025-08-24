package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateFranchiseRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.RequestValidator;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
    private final FranchiseServicePort franchiseUseCase;
    private final FranchiseMapper franchiseMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> franchiseUseCase.apply(body.name()))
                .map(franchiseMapper::toFranchiseResponse)
                .flatMap(fr -> ServerResponse
                        .created(URI.create(serverRequest.uri().toString().concat("/").concat(fr.id())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(fr))
                );
    }
}
