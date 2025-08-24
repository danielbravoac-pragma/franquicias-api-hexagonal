package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateFranchiseRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateFranchiseNameRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.FranchiseMapper;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.RequestValidator;
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

    public Mono<ServerResponse> updateName(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateFranchiseNameRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> franchiseUseCase.updateName(body.franchiseId(), body.name()))
                .map(franchiseMapper::toFranchiseResponse)
                .flatMap(fr -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(fr))
                )
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }
}
