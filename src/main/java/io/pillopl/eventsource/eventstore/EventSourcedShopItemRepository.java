package io.pillopl.eventsource.eventstore;


import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.ShopItemRepository;
import io.pillopl.eventsource.domain.shopitem.events.DomainEvent;
import io.pillopl.eventsource.domain.shopitem.events.EventBusGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
public class EventSourcedShopItemRepository implements ShopItemRepository {

  private final EventStore eventStore;
  private final EventSerializer eventSerializer;
  private final EventBusGateway gateway;

  @Autowired
  public EventSourcedShopItemRepository(EventStore eventStore, EventSerializer eventSerializer, ApplicationEventPublisher eventPublisher, EventBusGateway gateway) {
    this.eventStore = eventStore;
    this.eventSerializer = eventSerializer;
    this.gateway = gateway;
  }

  @Override
  public ShopItem save(ShopItem aggregate) {
    final List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
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
        .stream()
        .filter(evt -> !evt.when().isAfter(at))
        .collect(toList()));
  }


  private List<DomainEvent> getRelatedEvents(UUID uuid) {
    return eventStore.getEventsForAggregate(uuid)
      .stream()
      .map(eventSerializer::deserialize)
      .collect(toList());
  }

}
