package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.FranchiseServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.model.Franchise;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateFranchiseRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateFranchiseNameRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.FranchiseResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.FranchiseMapper;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;

import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranchiseHandlerTest {

    FranchiseServicePort franchiseUseCase = mock(FranchiseServicePort.class);
    FranchiseMapper franchiseMapper = mock(FranchiseMapper.class);
    RequestValidator requestValidator = mock(RequestValidator.class);

    WebTestClient client;

    @BeforeEach
    void setUp() {
        var handler = new FranchiseHandler(franchiseUseCase, franchiseMapper, requestValidator);

        RouterFunction<ServerResponse> router = RouterFunctions.route()
                .POST("/api/franchises", handler::create)
                .PATCH("/api/franchises/name", handler::updateName)
                .build();

        client = WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("") // usamos paths absolutos
                .build();
    }

    // ---------- POST /api/franchises ----------

    @Test
    void create_should_return_201_location_and_body() {
        var req = new CreateFranchiseRequest("  Mi Franquicia  ");

        when(requestValidator.validate(any(CreateFranchiseRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var created = new Franchise("F1", "Mi Franquicia", Instant.now());
        when(franchiseUseCase.apply("  Mi Franquicia  ")).thenReturn(Mono.just(created));

        var resp = new FranchiseResponse(created.id(), created.name(), created.createdAt());
        when(franchiseMapper.toFranchiseResponse(created)).thenReturn(resp);

        client.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().value("Location", loc -> assertThat(loc).endsWith("/api/franchises/" + resp.id()))
                .expectBody(FranchiseResponse.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo("F1");
                    assertThat(r.name()).isEqualTo("Mi Franquicia");
                });

        verify(requestValidator).validate(any(CreateFranchiseRequest.class));
        verify(franchiseUseCase).apply("  Mi Franquicia  ");
        verify(franchiseMapper).toFranchiseResponse(created);
    }

    // ---------- PATCH /api/franchises/name ----------

    @Test
    void updateName_should_return_200_with_body() {
        var req = new UpdateFranchiseNameRequest("F1", "Nuevo Nombre");

        when(requestValidator.validate(any(UpdateFranchiseNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var updated = new Franchise("F1", "Nuevo Nombre", Instant.now());
        when(franchiseUseCase.updateName("F1", "Nuevo Nombre")).thenReturn(Mono.just(updated));

        var resp = new FranchiseResponse(updated.id(), updated.name(), updated.createdAt());
        when(franchiseMapper.toFranchiseResponse(updated)).thenReturn(resp);

        client.patch()
                .uri("/api/franchises/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(FranchiseResponse.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo("F1");
                    assertThat(r.name()).isEqualTo("Nuevo Nombre");
                });

        verify(requestValidator).validate(any(UpdateFranchiseNameRequest.class));
        verify(franchiseUseCase).updateName("F1", "Nuevo Nombre");
        verify(franchiseMapper).toFranchiseResponse(updated);
    }

    @Test
    void updateName_should_return_400_when_franchise_not_found() {
        var req = new UpdateFranchiseNameRequest("F404", "X");

        when(requestValidator.validate(any(UpdateFranchiseNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(franchiseUseCase.updateName("F404", "X"))
                .thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND: F404")));

        client.patch()
                .uri("/api/franchises/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(err -> assertThat(err.message()).contains("DATA_NOT_FOUND"));

        verify(franchiseUseCase).updateName("F404", "X");
    }
}
