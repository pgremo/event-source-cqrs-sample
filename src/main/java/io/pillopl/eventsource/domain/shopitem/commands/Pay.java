package io.pillopl.eventsource.domain.shopitem.commands;

import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Pay {

  private final UUID uuid;
  private final LocalDateTime when;

}
