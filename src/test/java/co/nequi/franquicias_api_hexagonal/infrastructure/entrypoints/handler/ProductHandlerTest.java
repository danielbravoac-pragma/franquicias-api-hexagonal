package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler;

import co.nequi.franquicias_api_hexagonal.domain.api.ProductServicePort;
import co.nequi.franquicias_api_hexagonal.domain.exceptions.DataNotFoundException;
import co.nequi.franquicias_api_hexagonal.domain.model.BranchTopProduct;
import co.nequi.franquicias_api_hexagonal.domain.model.Product;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.CreateProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.DeleteProductRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateProductNameRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.UpdateStockRequest;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.ProductResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.mapper.ProductMapper;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductHandlerTest {

    ProductServicePort productUseCase = mock(ProductServicePort.class);
    ProductMapper productMapper = mock(ProductMapper.class);
    RequestValidator requestValidator = mock(RequestValidator.class);

    WebTestClient client;

    @BeforeEach
    void setUp() {
        var handler = new ProductHandler(productUseCase, productMapper, requestValidator);

        RouterFunction<ServerResponse> router = RouterFunctions.route()
                .POST("/api/products", handler::create)
                .PATCH("/api/products/stock", handler::patchStock)
                .DELETE("/api/products", handler::delete)
                .GET("/api/franchises/{franchiseId}/branches/max-stock", handler::topPerBranchForFranchise)
                .PATCH("/api/products/name", handler::updateName)
                .build();

        client = WebTestClient.bindToRouterFunction(router)
                .configureClient()
                .baseUrl("")
                .build();
    }

    // ---------- POST /api/products ----------

    @Test
    void create_should_return_201_location_and_body() {
        var req = new CreateProductRequest("B1", "Osito", 10);

        when(requestValidator.validate(any(CreateProductRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var mapped = new Product(null, "B1", null, "Osito", 10, null, null);
        when(productMapper.toProduct(req)).thenReturn(mapped);

        var created = new Product("F1", "B1", "P1", "Osito", 10, Instant.now(), null);
        when(productUseCase.add(mapped)).thenReturn(Mono.just(created));


        var resp = new ProductResponse(created.id(),
                created.name(), created.stock(), created.franchiseId(), created.branchId(), created.createdAt(), created.updatedAt());
        when(productMapper.toProductResponse(created)).thenReturn(resp);

        client.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().valueMatches("Location", ".*/api/products/P1$")
                .expectBody(ProductResponse.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo("P1");
                    assertThat(r.branchId()).isEqualTo("B1");
                    assertThat(r.franchiseId()).isEqualTo("F1");
                    assertThat(r.name()).isEqualTo("Osito");
                    assertThat(r.stock()).isEqualTo(10);
                });

        verify(productUseCase).add(mapped);
    }

    @Test
    void create_should_return_400_when_branch_not_found() {
        var req = new CreateProductRequest("B404", "X", 1);

        when(requestValidator.validate(any(CreateProductRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(productMapper.toProduct(req)).thenReturn(new Product(null, "B404", null, "X", 1, null, null));
        when(productUseCase.add(any())).thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND: B404")));

        client.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(e -> assertThat(e.message()).contains("DATA_NOT_FOUND"));
    }

    // ---------- PATCH /api/products/stock ----------

    @Test
    void patchStock_should_return_200_and_body() {
        var req = new UpdateStockRequest(42, "B1", "P1");

        when(requestValidator.validate(any(UpdateStockRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var mapped = new Product(null, "B1", "P1", null, 42, null, null);
        when(productMapper.toProductFromUpdateStock(req)).thenReturn(mapped);

        var updated = new Product("F1", "B1", "P1", "Osito", 42, Instant.now(), null);
        when(productUseCase.updateStock(mapped)).thenReturn(Mono.just(updated));

        var resp = new ProductResponse(updated.id(),
                updated.name(), updated.stock(), updated.franchiseId(), updated.branchId(), updated.createdAt(), updated.updatedAt());
        when(productMapper.toProductResponse(updated)).thenReturn(resp);

        client.patch()
                .uri("/api/products/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo("P1");
                    assertThat(r.stock()).isEqualTo(42);
                });

        verify(productUseCase).updateStock(mapped);
    }

    @Test
    void patchStock_should_return_400_when_branch_not_found() {
        var req = new UpdateStockRequest( 1,"B404", "P1");

        when(requestValidator.validate(any(UpdateStockRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(productMapper.toProductFromUpdateStock(req)).thenReturn(new Product(null, "B404", "P1", null, 1, null, null));
        when(productUseCase.updateStock(any())).thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND: B404")));

        client.patch()
                .uri("/api/products/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(e -> assertThat(e.message()).contains("DATA_NOT_FOUND"));
    }

    // ---------- DELETE /api/products ----------

    @Test
    void delete_should_return_204() {
        var req = new DeleteProductRequest("B1", "P1");

        when(requestValidator.validate(any(DeleteProductRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var mapped = new Product(null, "B1", "P1", null, 0, null, null);
        when(productMapper.toProductFromDeleteProduct(req)).thenReturn(mapped);
        when(productUseCase.delete(mapped)).thenReturn(Mono.empty());

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isNoContent();

        verify(productUseCase).delete(mapped);
    }

    @Test
    void delete_should_return_400_when_not_found() {
        var req = new DeleteProductRequest("B404", "P1");

        when(requestValidator.validate(any(DeleteProductRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(productMapper.toProductFromDeleteProduct(req))
                .thenReturn(new Product(null, "B404", "P1", null, 0, null, null));
        when(productUseCase.delete(any())).thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND")));

        client.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(e -> assertThat(e.message()).contains("DATA_NOT_FOUND"));
    }

    // ---------- GET /api/franchises/{franchiseId}/branches/max-stock ----------

    @Test
    void topPerBranchForFranchise_should_return_200_list() {
        var fid = "F1";
        var p = new Product(fid, "B1", "P9", "Max", 99, Instant.now(), null);
        var t1 = new BranchTopProduct("B1", "Centro", p);
        var t2 = new BranchTopProduct("B2", "Norte", null);

        when(productUseCase.findTopPerBranchForFranchise(fid))
                .thenReturn(Flux.just(t1, t2));

        client.get()
                .uri("/api/franchises/{franchiseId}/branches/max-stock", fid)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BranchTopProduct.class)
                .value(list -> {
                    assertThat(list).hasSize(2);
                    var byId = list.stream().collect(java.util.stream.Collectors.toMap(BranchTopProduct::branchId, x -> x));
                    assertThat(byId.get("B1").product().name()).isEqualTo("Max");
                    assertThat(byId.get("B2").product()).isNull();
                });

        verify(productUseCase).findTopPerBranchForFranchise(fid);
    }

    // ---------- PATCH /api/products/name ----------

    @Test
    void updateName_should_return_200_and_body() {
        var req = new UpdateProductNameRequest("P1", "Nuevo");

        when(requestValidator.validate(any(UpdateProductNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var updated = new Product("F1", "B1", "P1", "Nuevo", 10, Instant.now(), null);
        when(productUseCase.updateName("Nuevo", "P1")).thenReturn(Mono.just(updated));

        var resp = new ProductResponse(updated.id(),
                updated.name(), updated.stock(), updated.franchiseId(), updated.branchId(), updated.createdAt(), updated.updatedAt());
        when(productMapper.toProductResponse(updated)).thenReturn(resp);

        client.patch()
                .uri("/api/products/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo("P1");
                    assertThat(r.name()).isEqualTo("Nuevo");
                });

        verify(productUseCase).updateName("Nuevo", "P1");
    }

    @Test
    void updateName_should_return_400_when_product_not_found() {
        var req = new UpdateProductNameRequest("P404", "X");

        when(requestValidator.validate(any(UpdateProductNameRequest.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(productUseCase.updateName("X", "P404"))
                .thenReturn(Mono.error(new DataNotFoundException("DATA_NOT_FOUND: P404")));

        client.patch()
                .uri("/api/products/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(e -> assertThat(e.message()).contains("DATA_NOT_FOUND"));
    }
}
