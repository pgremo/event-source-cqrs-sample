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

  private final EventStore eventStore;
  private final EventSerializer eventSerializer;
  private final EventGateway gateway;

  @Autowired
  public ShopItemRepository(EventStore eventStore, EventSerializer eventSerializer, EventGateway gateway) {
    this.eventStore = eventStore;
    this.eventSerializer = eventSerializer;
    this.gateway = gateway;
  }

  @Override
  public ShopItem save(ShopItem aggregate) {
    List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
    eventStore.saveEvents(
      aggregate.getUuid(),
      pendingEvents
        .stream()
        .map(eventSerializer::serialize)
        .collect(toList()));
    pendingEvents.forEach(gateway::publish);
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
    return eventStore.getEventsForAggregate(uuid)
      .stream()
      .map(eventSerializer::deserialize);
  }

}
