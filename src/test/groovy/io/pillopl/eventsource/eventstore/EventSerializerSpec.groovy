package io.pillopl.eventsource.eventstore

import com.fasterxml.jackson.databind.ObjectMapper
import io.pillopl.eventsource.domain.shopitem.events.DomainEvent
import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Subject

import static java.time.LocalDateTime.now

class EventSerializerSpec extends Specification {

  final String ANY_TYPE = "ANY_TYPE"
  final UUID ANY_UUID = UUID.fromString("9a94d251-5fdb-4f38-b308-9f72d2355467")

  @Subject
  EventSerializer eventSerializer = new EventSerializer(new ObjectMapper().findAndRegisterModules())

  def "should parse ItemBought event"() {
    given:
    String body = """{
                "type": "$ItemBought.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
    when:
    DomainEvent event = eventSerializer.deserialize(new EventDescriptor(body, ANY_UUID, now(), ANY_TYPE))
    then:
    event.uuid == ANY_UUID
  }

  def "should parse ItemPaid event"() {
    given:
    String body = """{
                "type": "$ItemPaid.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
    when:
    DomainEvent event = eventSerializer.deserialize(new EventDescriptor(body, ANY_UUID, now(), ANY_TYPE))
    then:
    event.uuid == ANY_UUID
  }

  def "should parse ItemPaymentTimeout event"() {
    given:
    String body = """{
                "type": "$ItemPaymentTimeout.TYPE",
                "uuid": "$ANY_UUID",
                "when": "2016-05-24T12:06:41.045Z"
                }"""
    when:
    DomainEvent event = eventSerializer.deserialize(new EventDescriptor(body, ANY_UUID, now(), ANY_TYPE))
    then:
    event.uuid == ANY_UUID
  }

}
