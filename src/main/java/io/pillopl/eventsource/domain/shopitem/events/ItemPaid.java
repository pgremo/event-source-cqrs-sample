package io.pillopl.eventsource.domain.shopitem.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPaid implements DomainEvent {

  public static final String TYPE = "item.paid";

  private UUID uuid;
  private LocalDateTime when;

  @Override
  public String type() {
    return TYPE;
  }

  @Override
  public LocalDateTime when() {
    return when;
  }

  @Override
  public UUID uuid() {
    return uuid;
  }
}
