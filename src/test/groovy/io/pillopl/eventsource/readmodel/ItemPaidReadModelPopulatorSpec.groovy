package io.pillopl.eventsource.readmodel

import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

import static java.time.LocalDateTime.now
import static java.util.UUID.randomUUID

class ItemPaidReadModelPopulatorSpec extends Specification {

  private static final UUID ANY_UUID = randomUUID()
  private static final LocalDateTime ANY_DATE = now()

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
