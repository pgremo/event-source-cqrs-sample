package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ItemBoughtEventHandler {

  private final JdbcReadModel jdbcReadModelUpdater;

  @Autowired
  public ItemBoughtEventHandler(JdbcReadModel jdbcReadModelUpdater) {
    this.jdbcReadModelUpdater = jdbcReadModelUpdater;
  }

  @Transactional
  @ServiceActivator(inputChannel = "events-in")
  public void handle(ItemBought item) {
    jdbcReadModelUpdater.updateOrCreateItemAsBlocked(item.uuid(), item.when(), item.getPaymentTimeoutDate());
  }
}
