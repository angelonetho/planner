package dev.netho.planner.trip;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripData(UUID id, String destination, LocalDateTime starts_at, LocalDateTime ends_at,
                       String owner_email, String owner_name, Boolean is_confirmed) {
}
