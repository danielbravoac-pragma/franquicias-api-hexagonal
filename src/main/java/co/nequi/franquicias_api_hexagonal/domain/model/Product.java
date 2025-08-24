package co.nequi.franquicias_api_hexagonal.domain.model;

import java.time.Instant;

public record Product(String franchiseId,
                      String branchId,
                      String id,
                      String name,
                      Integer stock,
                      Instant createdAt,
                      Instant updatedAt) {
}
