package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReadModelOnDomainEventUpdater {

  private final JdbcReadModel jdbcReadModelUpdater;

  @Autowired
  public ReadModelOnDomainEventUpdater(JdbcReadModel jdbcReadModelUpdater) {
    this.jdbcReadModelUpdater = jdbcReadModelUpdater;
  }

  @Transactional
  public void handle(ItemPaymentTimeout item) {
    jdbcReadModelUpdater.updateItemAsPaymentMissing(item.uuid(), item.when());
  }

  @Transactional
  public void handle(ItemPaid item) {
    jdbcReadModelUpdater.updateItemAsPaid(item.uuid(), item.when());
  }

  @Transactional
  public void handle(ItemBought item) {
    jdbcReadModelUpdater.updateOrCreateItemAsBlocked(item.uuid(), item.when(), item.getPaymentTimeoutDate());
  }

}
