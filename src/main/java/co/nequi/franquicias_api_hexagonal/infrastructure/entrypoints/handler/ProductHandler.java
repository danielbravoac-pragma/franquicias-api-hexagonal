package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.ProductServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.DeleteProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateProductNameRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateStockRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.ProductMapper;
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
public class ProductHandler {
    private final ProductServicePort productUseCase;
    private final ProductMapper productMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateProductRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> productUseCase.add(productMapper.toProduct(body)))
                .map(productMapper::toProductResponse)
                .flatMap(pr -> ServerResponse
                        .created(URI.create(serverRequest.uri().toString().concat("/").concat(pr.id())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(pr))
                ).onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }

    public Mono<ServerResponse> patchStock(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateStockRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> productUseCase.updateStock(productMapper.toProductFromUpdateStock(body)))
                .map(productMapper::toProductResponse)
                .flatMap(pr -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(pr))
                )
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(DeleteProductRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> productUseCase.delete(productMapper.toProductFromDeleteProduct(body)))
                .flatMap(pr -> ServerResponse
                        .noContent().build()
                )
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }

    public Mono<ServerResponse> topPerBranchForFranchise(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable("franchiseId");
        return productUseCase.findTopPerBranchForFranchise(franchiseId)
                .collectList()
                .flatMap(pr -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(pr))
                )
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }

    public Mono<ServerResponse> updateName(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateProductNameRequest.class)
                .flatMap(requestValidator::validate)
                .flatMap(body -> productUseCase.updateName(body.name(), body.productId()))
                .map(productMapper::toProductResponse)
                .flatMap(pr -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(pr)))
                .onErrorResume(DataNotFoundException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new ErrorResponse("400", e.getMessage())));
    }
}
