package io.pillopl.eventsource.eventstore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventDescriptorRepository extends JpaRepository<EventDescriptor, Long> {
  Stream<EventDescriptor> findByAggregateIdOrderById(UUID id);
  Stream<EventDescriptor> findByAggregateIdAndOccurredAtLessThanEqualOrderById(UUID id, LocalDateTime at);
}
