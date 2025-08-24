package co.nequi.franquicias_api_hexagonal.domain.model;

import java.time.Instant;

public record Franchise(String id,
                        String name,
                        Instant createdAt) {
}
