package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ItemPaidEventHandler {

  private final JdbcReadModel jdbcReadModelUpdater;

  @Autowired
  public ItemPaidEventHandler(JdbcReadModel jdbcReadModelUpdater) {
    this.jdbcReadModelUpdater = jdbcReadModelUpdater;
  }

  @Transactional
  @ServiceActivator(inputChannel = "events")
  public void handle(ItemPaid item) {
    jdbcReadModelUpdater.updateItemAsPaid(item.uuid(), item.when());
  }
}
