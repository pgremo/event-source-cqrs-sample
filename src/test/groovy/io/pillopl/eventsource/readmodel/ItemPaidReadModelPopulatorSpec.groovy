package io.pillopl.eventsource.readmodel

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

import static java.time.Instant.now
import static java.util.UUID.randomUUID

class ItemPaidReadModelPopulatorSpec extends Specification {

  private static final UUID ANY_UUID = randomUUID()
  private static final Instant ANY_DATE = now()
  private static final Instant ANY_PAYMENT_TIMEOUT = now()

  JdbcReadModel jdbcReadModel = Mock()

  @Subject
  ItemPaidEventHandler ReadModelUpdater = new ItemPaidEventHandler(jdbcReadModel)

  def 'should update item when receiving item paid event'() {
    when:
    ReadModelUpdater.handle(new ItemPaid(ANY_UUID, ANY_DATE))
    then:
    1 * jdbcReadModel.updateItemAsPaid(ANY_UUID, ANY_DATE)
  }
}
