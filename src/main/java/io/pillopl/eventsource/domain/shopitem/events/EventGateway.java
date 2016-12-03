package io.pillopl.eventsource.domain.shopitem.events;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EventGateway {
  @Gateway(requestChannel = "events")
  void publish(Object message);
}
