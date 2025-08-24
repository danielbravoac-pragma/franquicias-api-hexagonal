package co.nequi.franquicias_api_hexagonal.domain.model;

import java.time.Instant;

public record Branch(String franchiseId,
                     String id,
                     String name,
                     Instant createdAt) {
}
