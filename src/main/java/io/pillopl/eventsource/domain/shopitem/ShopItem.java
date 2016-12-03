package io.pillopl.eventsource.domain.shopitem;

import io.pillopl.eventsource.domain.shopitem.events.DomainEvent;
import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;
import lombok.Getter;
import lombok.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static io.pillopl.eventsource.domain.shopitem.ShopItemState.*;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Value
public class ShopItem {

  @Getter
  private final UUID uuid;
  private final List<DomainEvent> changes;
  private final ShopItemState state;

  public ShopItem buy(UUID uuid, Instant when, int hoursToPaymentTimeout) {
    if (state == INITIALIZED) {
      return applyChange(new ItemBought(uuid, when, calculatePaymentTimeoutDate(when, hoursToPaymentTimeout)));
    } else {
      return this;
    }
  }

  private Instant calculatePaymentTimeoutDate(Instant boughtAt, int hoursToPaymentTimeout) {
    final Instant paymentTimeout = boughtAt.plus(hoursToPaymentTimeout, ChronoUnit.HOURS);
    if (paymentTimeout.isBefore(boughtAt)) {
      throw new IllegalArgumentException("Payment timeout day is before buying date!");
    }
    return paymentTimeout;
  }

  public ShopItem pay(Instant when) {
    throwIfStateIs(INITIALIZED, "Cannot pay for not existing item");
    return state == PAID ? this : applyChange(new ItemPaid(uuid, when));
  }

  public ShopItem markTimeout(Instant when) {
    throwIfStateIs(INITIALIZED, "Payment is not missing yet");
    throwIfStateIs(PAID, "Item already paid");
    return state == BOUGHT ? applyChange(new ItemPaymentTimeout(uuid, when)) : this;
  }

  private void throwIfStateIs(ShopItemState unexpectedState, String msg) {
    if (state == unexpectedState) {
      throw new IllegalStateException(String.format("%s UUID: %s", msg, uuid));
    }
  }

  private ShopItem apply(ItemBought event) {
    return new ShopItem(event.getUuid(), changes, BOUGHT);
  }

  private ShopItem apply(ItemPaid event) {
    return new ShopItem(event.getUuid(), changes, PAID);
  }

  private ShopItem apply(ItemPaymentTimeout event) {
    return new ShopItem(event.getUuid(), changes, PAYMENT_MISSING);
  }

  public static ShopItem from(UUID uuid, List<DomainEvent> history) {
    return history
      .stream()
      .reduce(
        new ShopItem(uuid, emptyList(), INITIALIZED),
        (tx, event) -> tx.applyChange(event, false),
        (t1, t2) -> {
          throw new UnsupportedOperationException();
        }
      );
  }

  private ShopItem applyChange(DomainEvent event, boolean isNew) {
    final ShopItem item = apply(event);
    if (isNew) {
      return new ShopItem(item.getUuid(), appendChange(item, event), item.getState());
    } else {
      return item;
    }
  }

  private List<DomainEvent> appendChange(ShopItem item, DomainEvent event) {
    return concat(item.getChanges().stream(), of(event)).collect(toList());
  }

  private ShopItem apply(DomainEvent event) {
    if (event instanceof ItemPaid) {
      return apply((ItemPaid) event);
    } else if (event instanceof ItemBought) {
      return apply((ItemBought) event);
    } else if (event instanceof ItemPaymentTimeout) {
      return apply((ItemPaymentTimeout) event);
    } else {
      throw new IllegalArgumentException(String.format("Cannot handle event %s", event.getClass()));
    }
  }

  private ShopItem applyChange(DomainEvent event) {
    return applyChange(event, true);
  }

  public List<DomainEvent> getUncommittedChanges() {
    return changes;
  }

  public ShopItem markChangesAsCommitted() {
    return new ShopItem(uuid, emptyList(), state);
  }

}
