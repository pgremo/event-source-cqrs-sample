package io.pillopl.eventsource.integration.readmodel

import io.pillopl.eventsource.boundary.ShopItems
import io.pillopl.eventsource.integration.IntegrationSpec
import io.pillopl.eventsource.readmodel.JdbcReadModel
import io.pillopl.eventsource.readmodel.ShopItemView
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.LocalDateTime

import static io.pillopl.eventsource.CommandFixture.*
import static java.time.LocalDateTime.parse

class JdbcReadModelIntegrationSpec extends IntegrationSpec {

  private static final LocalDateTime ANY_TIME = parse("1995-10-23T10:12:35")
  private static final LocalDateTime ANY_OTHER_TIME = ANY_TIME.plusSeconds(100)
  private static final LocalDateTime YET_ANOTHER_TIME = ANY_OTHER_TIME.plusSeconds(100)

  @Subject
  @Autowired
  JdbcReadModel readModel

  @Autowired
  ShopItems shopItems

  def 'should store new bought item'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.uuid == itemUUID.toString()
    tx.status == 'BOUGHT'
    tx.when_bought.toLocalDateTime() == ANY_TIME
    tx.when_paid == null
    tx.when_payment_timeout.toLocalDateTime() == parse("1995-10-24T10:12:35")
    tx.when_payment_marked_as_missing == null
  }

  def 'buying should be idempotent on read side'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    and:
    shopItems.buy(buyItemCommand(itemUUID, ANY_OTHER_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.when_bought.toLocalDateTime() == ANY_TIME
  }

  def 'should update item as paid'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    and:
    shopItems.pay(payItemCommand(itemUUID, ANY_OTHER_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.when_paid.toLocalDateTime() == ANY_OTHER_TIME
  }

  def 'paying should be idempotent on read side'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    and:
    shopItems.pay(payItemCommand(itemUUID, ANY_OTHER_TIME))
    and:
    shopItems.pay(payItemCommand(itemUUID, YET_ANOTHER_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.when_paid.toLocalDateTime() == ANY_OTHER_TIME
  }

  def 'should update item as payment missed'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    and:
    shopItems.markPaymentTimeout(markPaymentTimeoutCommand(itemUUID, ANY_OTHER_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.when_payment_marked_as_missing.toLocalDateTime() == ANY_OTHER_TIME
  }

  def 'updating item as payment missed should be idempotent'() {
    when:
    UUID itemUUID = UUID.randomUUID()
    shopItems.buy(buyItemCommand(itemUUID, ANY_TIME))
    and:
    shopItems.markPaymentTimeout(markPaymentTimeoutCommand(itemUUID, ANY_OTHER_TIME))
    and:
    shopItems.markPaymentTimeout(markPaymentTimeoutCommand(itemUUID, YET_ANOTHER_TIME))
    then:
    ShopItemView tx = readModel.getItemBy(itemUUID)
    tx.when_payment_marked_as_missing.toLocalDateTime() == ANY_OTHER_TIME
  }

}
