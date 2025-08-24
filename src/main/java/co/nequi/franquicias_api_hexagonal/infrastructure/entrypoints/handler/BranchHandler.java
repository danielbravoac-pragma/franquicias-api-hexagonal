package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.usecase.BranchUseCase;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateBranchRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.exception.RequestValidator;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.BranchMapper;
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
public class BranchHandler {

    private final BranchUseCase branchUseCase;
    private final BranchMapper branchMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateBranchRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> branchUseCase.apply(branchMapper.toBranch(body)))
                .map(branchMapper::toBranchResponse)
                .flatMap(br -> ServerResponse
                        .created(URI.create(serverRequest.uri().toString().concat("/").concat(br.id())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(br)))
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }
}
