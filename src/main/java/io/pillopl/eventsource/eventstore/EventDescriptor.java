package io.pillopl.eventsource.eventstore;

import lombok.Getter;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class EventDescriptor {

  @Id
  @GeneratedValue(generator = "event_descriptors_seq", strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "event_descriptors_seq", sequenceName = "event_descriptors_seq", allocationSize = 1)
  @Getter
  private Long id;

  @Getter
  @Column(nullable = false)
  private UUID aggregateId;

  @Getter
  @Column(nullable = false, length = 600)
  private String body;

  @Getter
  @Column(nullable = false)
  private Instant occurredAt = Instant.now();

  @Getter
  @Column(nullable = false, length = 60)
  private String type;

  EventDescriptor(String body, UUID aggregateId, Instant occurredAt, String type) {
    this.body = body;
    this.aggregateId = aggregateId;
    this.occurredAt = occurredAt;
    this.type = type;
  }

  @SuppressWarnings("unused")
  private EventDescriptor() {
  }
}
