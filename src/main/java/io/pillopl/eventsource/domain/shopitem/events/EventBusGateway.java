package io.pillopl.eventsource.domain.shopitem.events;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

@MessagingGateway
public interface EventBusGateway {
  @Gateway(requestChannel = "events")
  void publish(DomainEvent message);
}
