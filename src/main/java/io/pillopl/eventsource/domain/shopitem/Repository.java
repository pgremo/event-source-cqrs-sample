package io.pillopl.eventsource.domain.shopitem;

import java.time.Instant;
import java.util.UUID;

public interface Repository<T, K> {

  T save(T aggregate);

  T load(K uuid);

  T loadFrom(K uuid, Instant at);

}
