package io.pillopl.eventsource.eventstore;


import io.pillopl.eventsource.domain.shopitem.Repository;
import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.events.DomainEvent;
import io.pillopl.eventsource.domain.shopitem.events.EventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class ShopItemRepository implements Repository<ShopItem> {

  private final EventDescriptorRepository eventStreamStore;
  private final EventSerializer eventSerializer;
  private final EventGateway gateway;

  @Autowired
  public ShopItemRepository(EventDescriptorRepository eventStreamStore, EventSerializer eventSerializer, EventGateway gateway) {
    this.eventStreamStore = eventStreamStore;
    this.eventSerializer = eventSerializer;
    this.gateway = gateway;
  }

  @Override
  public ShopItem save(ShopItem aggregate) {
    List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
    eventStreamStore.save(pendingEvents
        .stream()
        .map(eventSerializer::serialize)
        .collect(toList()));
    gateway.publish(pendingEvents);
    return aggregate.markChangesAsCommitted();
  }

  @Override
  public ShopItem getByUUID(UUID uuid) {
    return ShopItem.from(uuid, getRelatedEvents(uuid));
  }

  @Override
  public ShopItem getByUUIDat(UUID uuid, Instant at) {
    return ShopItem.from(uuid,
      getRelatedEvents(uuid)
        .filter(evt -> !evt.when().isAfter(at)));
  }

  private Stream<DomainEvent> getRelatedEvents(UUID uuid) {
    return eventStreamStore.findByAggregateIdOrderById(uuid)
      .map(eventSerializer::deserialize);
  }

}
