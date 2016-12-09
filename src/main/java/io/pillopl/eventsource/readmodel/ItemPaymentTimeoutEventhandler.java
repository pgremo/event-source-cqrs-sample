package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ItemPaymentTimeoutEventhandler {

  private final JdbcReadModel jdbcReadModelUpdater;

  @Autowired
  public ItemPaymentTimeoutEventhandler(JdbcReadModel jdbcReadModelUpdater) {
    this.jdbcReadModelUpdater = jdbcReadModelUpdater;
  }

  @Transactional
  @ServiceActivator(inputChannel = "events-in")
  public void handle(ItemPaymentTimeout item) {
    jdbcReadModelUpdater.updateItemAsPaymentMissing(item.uuid(), item.when());
  }
}
