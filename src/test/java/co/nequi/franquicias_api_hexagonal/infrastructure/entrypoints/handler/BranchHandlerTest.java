package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.BranchServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.model.Branch;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateBranchRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateBranchNameRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.BranchResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.BranchMapper;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BranchHandlerTest {

    BranchServicePort branchUseCase = mock(BranchServicePort.class);
    BranchMapper branchMapper = mock(BranchMapper.class);
    RequestValidator requestValidator = mock(RequestValidator.class);

    WebTestClient client;

    @BeforeEach
    void setUp() {
        var handler = new BranchHandler(branchUseCase, branchMapper, requestValidator);

        RouterFunction<ServerResponse> router = RouterFunctions.route()
                .POST("/api/branches", handler::create)
                .PATCH("/api/branches/name", handler::updateName)
                .build();

        client = WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("")
                .build();
    }

    // ---------- POST /api/branches ----------

    @Test
    void create_should_return_201_and_location_and_body() {
        // given
        var req = new CreateBranchRequest("F1", "Sucursal Centro");


        when(requestValidator.validate(any(CreateBranchRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));


        var mapped = new Branch("F1", null, "Sucursal Centro", null);
        when(branchMapper.toBranch(req)).thenReturn(mapped);

        var created = new Branch("F1", "B1", "Sucursal Centro", Instant.now());
        when(branchUseCase.apply(mapped)).thenReturn(Mono.just(created));

        var resp = new BranchResponse(created.id(), created.name(), created.franchiseId(), created.createdAt());
        when(branchMapper.toBranchResponse(created)).thenReturn(resp);

        // when / then
        client.post()
                .uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().value("Location", loc ->
                        assertThat(loc).endsWith("/api/branches/" + resp.id()))
                .expectBody(BranchResponse.class)
                .value(b -> {
                    assertThat(b.id()).isEqualTo("B1");
                    assertThat(b.franchiseId()).isEqualTo("F1");
                    assertThat(b.name()).isEqualTo("Sucursal Centro");
                });

        // Verifica que se pasó por validator y mapper
        verify(requestValidator).validate(any(CreateBranchRequest.class));
        ArgumentCaptor<CreateBranchRequest> capReq = ArgumentCaptor.forClass(CreateBranchRequest.class);
        verify(branchMapper).toBranch(capReq.capture());
        assertThat(capReq.getValue().franchiseId()).isEqualTo("F1");
        verify(branchUseCase).apply(mapped);
        verify(branchMapper).toBranchResponse(created);
    }

    @Test
    void create_should_return_400_when_franchise_not_found() {
        var req = new CreateBranchRequest("F404", "X");

        when(requestValidator.validate(any(CreateBranchRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(branchMapper.toBranch(req)).thenReturn(new Branch("F404", null, "X", null));
        when(branchUseCase.apply(any())).thenReturn(
                Mono.error(new DataNotFoundException("DATA_NOT_FOUND: franchiseIdF404"))
        );

        client.post()
                .uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ErrorResponse.class)
                .value(err -> {
                    assertThat(err.code()).isEqualTo("400");
                    assertThat(err.message()).contains("DATA_NOT_FOUND");
                });

        verify(branchUseCase).apply(any());
    }

    // ---------- PATCH /api/branches/name ----------

    @Test
    void updateName_should_return_200_with_body() {
        var req = new UpdateBranchNameRequest("B1", "Nuevo Nombre");

        when(requestValidator.validate(any(UpdateBranchNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var updated = new Branch("F1", "B1", "Nuevo Nombre", Instant.now());
        when(branchUseCase.updateName("B1", "Nuevo Nombre")).thenReturn(Mono.just(updated));

        var resp = new BranchResponse(updated.id(), updated.name(), updated.franchiseId(), updated.createdAt());
        when(branchMapper.toBranchResponse(updated)).thenReturn(resp);

        client.patch()
                .uri("/api/branches/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BranchResponse.class)
                .value(b -> {
                    assertThat(b.id()).isEqualTo("B1");
                    assertThat(b.name()).isEqualTo("Nuevo Nombre");
                });

        verify(requestValidator).validate(any(UpdateBranchNameRequest.class));
        verify(branchUseCase).updateName("B1", "Nuevo Nombre");
        verify(branchMapper).toBranchResponse(updated);
    }

    @Test
    void updateName_should_return_400_when_branch_not_found() {
        var req = new UpdateBranchNameRequest("B404", "X");

        when(requestValidator.validate(any(UpdateBranchNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(branchUseCase.updateName("B404", "X"))
                .thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND: B404")));

        client.patch()
                .uri("/api/branches/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(err -> assertThat(err.message()).contains("DATA_NOT_FOUND"));
    }
}
