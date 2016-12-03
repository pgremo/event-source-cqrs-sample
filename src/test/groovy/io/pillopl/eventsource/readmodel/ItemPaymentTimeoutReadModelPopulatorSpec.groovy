package io.pillopl.eventsource.readmodel

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

import static java.time.Instant.now
import static java.util.UUID.randomUUID

class ItemPaymentTimeoutReadModelPopulatorSpec extends Specification {

  private static final UUID ANY_UUID = randomUUID()
  private static final Instant ANY_DATE = now()
  private static final Instant ANY_PAYMENT_TIMEOUT = now()

  JdbcReadModel jdbcReadModel = Mock()

  @Subject
  ItemPaymentTimeoutEventhandler ReadModelUpdater = new ItemPaymentTimeoutEventhandler(jdbcReadModel)

  def 'should update item when receiving payment timeout event'() {
    when:
    ReadModelUpdater.handle(new ItemPaymentTimeout(ANY_UUID, ANY_DATE))
    then:
    1 * jdbcReadModel.updateItemAsPaymentMissing(ANY_UUID, ANY_DATE)
  }
}
