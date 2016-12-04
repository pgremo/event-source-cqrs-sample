package io.pillopl.eventsource.domain.shopitem.commands;

import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class Buy {

  private final UUID uuid;
  private final BigDecimal amount;
  private final Instant when;

}
