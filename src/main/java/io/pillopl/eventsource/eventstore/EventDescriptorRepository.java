package io.pillopl.eventsource.eventstore;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.stream.Stream;

public interface EventDescriptorRepository extends JpaRepository<EventDescriptor, Long> {
  Stream<EventDescriptor> findByAggregateIdOrderById(UUID id);
}
