package co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints;

import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.request.*;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.BranchResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.BranchTopProductResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.FranchiseResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.dto.response.ProductResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.util.exception.ErrorResponse;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler.BranchHandler;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler.FranchiseHandler;
import co.nequi.franquicias_api_hexagonal.infrastructure.entrypoints.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @RouterOperations({
            @RouterOperation(
                    path = "/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class, beanMethod = "create",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a Franchise",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201",
                                            description = "Franchise created Successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    @Bean
    RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler franchiseHandler) {
        return route(POST("/franchises"), franchiseHandler::create);
    }

    @RouterOperations({
            @RouterOperation(
                    path = "/branches",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class, beanMethod = "create",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Create a Branch",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201",
                                            description = "Branch created Successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Franchise Not Found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    @Bean
    RouterFunction<ServerResponse> branchRoutes(BranchHandler branchHandler) {
        return route(POST("/branches"), branchHandler::create);
    }


    @RouterOperations({
            @RouterOperation(
                    path = "/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class, beanMethod = "create",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create a Product",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = CreateProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201",
                                            description = "Product created Successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Branch Not Found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/products/stock",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class, beanMethod = "patchStock",
                    operation = @Operation(
                            operationId = "updateStockProduct",
                            summary = "Update stock from a Product",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            description = "Product stock updated Successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Branch or Product Not Found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/products",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class, beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a Product",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = DeleteProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "204",
                                            description = "Product deleted Successfully"),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Branch or Product Not Found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/products/max/{franchiseId}",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class, beanMethod = "topPerBranchForFranchise",
                    operation = @Operation(
                            operationId = "updateStockProduct",
                            summary = "Get Top Stock Product per Branch For Franchises",
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = "franchiseId", required = true,
                                            description = "Franchise Id",
                                            schema = @Schema(type = "string", format = "uuid"),
                                            example = "c8b6e2ab-2b2d-41a3-9b6b-9d8f5b4a6f1d"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200",
                                            description = "Report collected Successfully",
                                            content = @Content(schema = @Schema(implementation = BranchTopProductResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Bad Request in Body",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400",
                                            description = "Franchise Not Found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500",
                                            description = "Internal Server Error",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),

    })
    @Bean
    RouterFunction<ServerResponse> productRoutes(ProductHandler productHandler) {
        return route(POST("/products"), productHandler::create)
                .andRoute(PATCH("/products/stock"), productHandler::patchStock)
                .andRoute(DELETE("/products"), productHandler::delete)
                .andRoute(GET("/products/max/{franchiseId}"), productHandler::topPerBranchForFranchise);
    }

}
