package io.pillopl.eventsource.domain.shopitem;

import java.time.LocalDateTime;

public interface Repository<T, K> {

  T save(T aggregate);

  T load(K uuid);

  T loadFrom(K uuid, LocalDateTime at);

}
