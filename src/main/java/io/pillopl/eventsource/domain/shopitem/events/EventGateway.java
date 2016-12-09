package io.pillopl.eventsource.domain.shopitem.events;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.Collection;

@MessagingGateway
public interface EventGateway {
  @Gateway(requestChannel = "events-out")
  void publish(Collection<DomainEvent> message);
}
