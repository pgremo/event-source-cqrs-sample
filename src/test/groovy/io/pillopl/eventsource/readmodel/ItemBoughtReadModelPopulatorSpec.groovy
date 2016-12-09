package io.pillopl.eventsource.readmodel

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

import static java.time.Instant.now
import static java.util.UUID.randomUUID

class ItemBoughtReadModelPopulatorSpec extends Specification {

  private static final UUID ANY_UUID = randomUUID()
  private static final Instant ANY_DATE = now()
  private static final Instant ANY_PAYMENT_TIMEOUT = now()

  JdbcReadModel jdbcReadModel = Mock()

  @Subject
  ItemBoughtEventHandler ReadModelUpdater = new ItemBoughtEventHandler(jdbcReadModel)

  def 'should update or create bought item when receiving bought item event'() {
    when:
    ReadModelUpdater.handle(new ItemBought(ANY_UUID, ANY_DATE, ANY_PAYMENT_TIMEOUT))
    then:
    1 * jdbcReadModel.updateOrCreateItemAsBlocked(ANY_UUID, ANY_DATE, ANY_PAYMENT_TIMEOUT)
  }
}
