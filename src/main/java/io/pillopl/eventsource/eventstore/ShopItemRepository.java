package io.pillopl.eventsource.eventstore;


import io.pillopl.eventsource.domain.shopitem.Repository;
import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.events.DomainEvent;
import io.pillopl.eventsource.domain.shopitem.events.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
public class ShopItemRepository implements Repository<ShopItem, UUID> {

  private final EventDescriptorRepository eventStore;
  private final EventSerializer eventSerializer;
  private final EventGateway gateway;

  @Autowired
  public ShopItemRepository(EventDescriptorRepository eventStore, EventSerializer eventSerializer, EventGateway gateway) {
    this.eventStore = eventStore;
    this.eventSerializer = eventSerializer;
    this.gateway = gateway;
  }

  @Override
  public ShopItem save(ShopItem aggregate) {
    List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
    eventStore.save(pendingEvents
      .stream()
      .map(eventSerializer::serialize)
      .collect(toList()));
    gateway.publish(pendingEvents);
    return aggregate.markChangesAsCommitted();
  }

  @Override
  public ShopItem load(UUID uuid) {
    return ShopItem.from(uuid, eventStore.findByAggregateIdOrderById(uuid)
      .map(eventSerializer::deserialize));
  }

  @Override
  public ShopItem loadFrom(UUID uuid, LocalDateTime at) {
    return ShopItem.from(uuid, eventStore.findByAggregateIdAndOccurredAtLessThanEqualOrderById(uuid, at)
      .map(eventSerializer::deserialize));
  }
}
