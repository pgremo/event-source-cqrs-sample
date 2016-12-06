package io.pillopl.eventsource.domain.shopitem;

import java.time.Instant;
import java.util.UUID;

public interface Repository<T> {

  T save(T aggregate);

  T getByUUID(UUID uuid);

  T getByUUIDat(UUID uuid, Instant at);

}
