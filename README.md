# Franquicias API — Hexagonal + WebFlux + DynamoDB

API reactiva para gestionar **franquicias**, **sucursales** y **productos** usando **Arquitectura Hexagonal**, **Spring
WebFlux** (RouterFunctions + Handlers) y **AWS DynamoDB** como almacenamiento.

> **Credenciales:** la app usa el **perfil `default` de tu AWS CLI** vía *Default Credentials Provider*. **No requiere
variables de entorno** adicionales.

## 🌐 Demo desplegada (ECS/ALB)

Ya hay una instancia online para que no tengas que levantar nada en local:

- **Swagger (online):** http://franchisesapiloadbalancer-10577079.us-east-2.elb.amazonaws.com/swagger-ui/index.html

- **Base URL:** `http://franchisesapiloadbalancer-10577079.us-east-2.elb.amazonaws.com`

> Puedes probar todos los endpoints desde ese Swagger.
---

## 🚀 Ejecutar local con Docker

### Prerrequisitos

* Docker instalado.
* Tu equipo con **AWS CLI** configurado (perfil `default` con permisos sobre DynamoDB).

    * Permisos mínimos: `dynamodb:GetItem, PutItem, UpdateItem, DeleteItem, Query, DescribeTable` sobre la tabla e
      índices que se indican abajo.

### 1) Build del artefacto e imagen

```bash
./gradlew clean build -x test
# Construir imagen local usando el Dockerfile del proyecto
docker build -t franquicias-api:local .
```

### 2) Ejecutar montando tus credenciales AWS (perfil `default`)

**macOS / Linux**

```bash
docker run --rm -p 8080:8080 \
  -v ~/.aws:/root/.aws:ro \
  franquicias-api:local
```

**Windows (PowerShell)**

```powershell
docker run --rm -p 8080:8080 `
  -v "$HOME\.aws:/root/.aws:ro" `
  franquicias-api:local
```

> Si tu archivo `~/.aws/config` ya define la región, no necesitas exportarla. La app tomará región y credenciales del
> perfil `default` automáticamente.

### 3) Probar

* **Swagger UI**:

    * [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    * o [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🗄️ DynamoDB — Esquema usado por la aplicación

* **Tabla**: `FranchisesNetwork`
* **Partition key (PK)**: `pk` *(String)*
* **Sort key (SK)**: `sk` *(String)*

### Patrones de claves y tipos de ítems

* **Franquicia (metadata)**
  `pk = FRANCHISE#{franchiseId}`
  `sk = METADATA`

* **Sucursal**
  `pk = FRANCHISE#{franchiseId}`
  `sk = BRANCH#{branchId}`

* **Producto**
  `pk = FRANCHISE#{franchiseId}`
  `sk = PRODUCT#{branchId}#{productId}`

> Atributos comunes: `franchiseId`, `branchId`, `productId`, `name`, `stock` *(Number)*, `createdAt`, `updatedAt` (según
> corresponda).

### GSIs

* **`GSI_BranchById`** → **PK**: `branchId` *(String)*, **SK**: `franchiseId` *(String)*
* **`GSI_BranchProductsByStock`** → **PK**: `branchId` *(String)*, **SK**: `stock` *(Number)*
  *(Usado para obtener el producto con mayor stock por sucursal)*
* **`GSI_ProductById`** → **PK**: `productId` *(String)*

> **Importante:** el atributo `stock` en los ítems debe ser **Number (N)** para que el índice
`GSI_BranchProductsByStock` funcione correctamente como sort key.

---

## 🔌 Endpoints expuestos (RouterRest)

### Franquicias

* `POST   /franchises`
  **Body**: `{ "name": "..." }` → **201 Created**
* `PATCH /franchises/name`
  **Body**: `{ "franchiseId": "...", "name": "..." }` → **200 OK**

### Sucursales

* `POST   /branches`
  **Body**: `{ "franchiseId": "...", "name": "..." }` → **201 Created**
* `PATCH  /branches/name`
  **Body**: `{ "branchId": "...", "name": "..." }` → **200 OK**

### Productos

* `POST   /products`
  **Body**: `{ "branchId": "...", "name": "...", "stock": 0 }` → **201 Created**
* `PATCH  /products/stock`
  **Body**: `{ "branchId": "...", "productId": "...", "stock": 1 }` → **200 OK**
* `PATCH  /products/name`
  **Body**: `{ "productId": "...", "name": "..." }` → **200 OK**
* `DELETE /products`
  **Body**: `{ "productId": "...", "branchId": "..." }` → **204 No Content**

### Reporte

* `GET    /products/max/{franchiseId}`
  → **200 OK** (lista de sucursales con su producto top por stock; puede venir `product=null` si no hay productos en la
  sucursal)

---

## 🧱 Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   ├── co/
│   │       ├── nequi/
│   │           ├── franquicias_api_hexagonal/
│   │               ├── application/
│   │               │   ├── config/
│   │               │       └── UseCasesConfig.java
│   │               ├── domain/
│   │               │   ├── api/
│   │               │   │   ├── BranchServicePort.java
│   │               │   │   ├── FranchiseServicePort.java
│   │               │   │   └── ProductServicePort.java
│   │               │   ├── constants/
│   │               │   ├── enums/
│   │               │   │   └── ErrorMessages.java
│   │               │   ├── exceptions/
│   │               │   │   └── DataNotFoundException.java
│   │               │   ├── model/
│   │               │   │   ├── Branch.java
│   │               │   │   ├── BranchTopProduct.java
│   │               │   │   ├── Franchise.java
│   │               │   │   └── Product.java
│   │               │   ├── spi/
│   │               │   │   ├── BranchPersistencePort.java
│   │               │   │   ├── FranchisePersistencePort.java
│   │               │   │   └── ProductPersistencePort.java
│   │               │   ├── usecase/
│   │               │       ├── BranchUseCase.java
│   │               │       ├── FranchiseUseCase.java
│   │               │       └── ProductUseCase.java
│   │               ├── infrastructure/
│   │               │   ├── adapters/
│   │               │   │   ├── persistenceadapter/
│   │               │   │       ├── dynamo/
│   │               │   │           ├── config/
│   │               │   │           │   ├── DynamoConfig.java
│   │               │   │           │   └── DynamoParams.java
│   │               │   │           ├── exception/
│   │               │   │           │   ├── DataAlreadyExists.java
│   │               │   │           │   └── MessageError.java
│   │               │   │           ├── repository/
│   │               │   │               ├── DynamoBranchRepository.java
│   │               │   │               ├── DynamoFranchiseRepository.java
│   │               │   │               └── DynamoProductRepository.java
│   │               │   ├── entrypoints/
│   │               │       ├── dto/
│   │               │       │   ├── request/
│   │               │       │   │   ├── CreateBranchRequest.java
│   │               │       │   │   ├── CreateFranchiseRequest.java
│   │               │       │   │   ├── CreateProductRequest.java
│   │               │       │   │   ├── DeleteProductRequest.java
│   │               │       │   │   ├── UpdateBranchNameRequest.java
│   │               │       │   │   ├── UpdateFranchiseNameRequest.java
│   │               │       │   │   ├── UpdateProductNameRequest.java
│   │               │       │   │   └── UpdateStockRequest.java
│   │               │       │   ├── response/
│   │               │       │       ├── BranchResponse.java
│   │               │       │       ├── BranchTopProductResponse.java
│   │               │       │       ├── FranchiseResponse.java
│   │               │       │       └── ProductResponse.java
│   │               │       ├── handler/
│   │               │       │   ├── BranchHandler.java
│   │               │       │   ├── FranchiseHandler.java
│   │               │       │   └── ProductHandler.java
│   │               │       ├── mapper/
│   │               │       │   ├── BranchMapper.java
│   │               │       │   ├── BranchTopProductMapper.java
│   │               │       │   ├── FranchiseMapper.java
│   │               │       │   └── ProductMapper.java
│   │               │       ├── util/
│   │               │       │   ├── config/
│   │               │       │   │   └── ResourceWebPropertiesConfig.java
│   │               │       │   ├── exception/
│   │               │       │       ├── ErrorMessage.java
│   │               │       │       ├── ErrorResponse.java
│   │               │       │       ├── RequestValidator.java
│   │               │       │       └── WebExceptionHandler.java
│   │               │       └── RouterRest.java
│   │               └── FranquiciasApiHexagonalApplication.java
│   ├── resources/
│       └── application.properties
├── test/
    ├── java/
        ├── co/
            ├── nequi/
                ├── franquicias_api_hexagonal/
                    ├── domain/
                    │   ├── usecase/
                    │       ├── BranchUseCaseTest.java
                    │       ├── FranchiseUseCaseTest.java
                    │       └── ProductUseCaseTest.java
                    ├── infrastructure/
                    │   ├── entrypoints/
                    │       ├── handler/
                    │           ├── BranchHandlerTest.java
                    │           ├── FranchiseHandlerTest.java
                    │           └── ProductHandlerTest.java
                    └── FranquiciasApiHexagonalApplicationTests.java
                         # Pruebas de use case y handlers (WebTestClient)
```

**Arquitectura Hexagonal**

* **Dominio**: modelos + puertos + casos de uso (sin dependencias de infraestructura).
* **Entradas**: capa Web (RouterFunctions + Handlers), validación y DTOs.
* **Salidas**: adaptador DynamoDB implementando los `spi` del dominio.

---

## 🧪 Pruebas

Ejecutar pruebas unitarias:

```bash
./gradlew test
```

* Use cases (dominio): JUnit5 + Mockito + Reactor Test.
* Handlers + Router: `WebTestClient.bindToRouterFunction(...)` (rápidos, sin levantar todo el contexto).

---

## 🔧 Troubleshooting (local)

* **`The provided key element does not match the schema`** → revisa que `pk/sk` y los prefijos (`FRANCHISE#`, `BRANCH#`,
  `PRODUCT#`) estén bien formados.
* **Error de tipos en `GSI_BranchProductsByStock`** → confirma que `stock` sea **Number (N)** en los ítems.
* **`AccessDeniedException`** en Dynamo → el perfil `default` necesita permisos sobre la tabla `FranchisesNetwork` y sus
  índices.
