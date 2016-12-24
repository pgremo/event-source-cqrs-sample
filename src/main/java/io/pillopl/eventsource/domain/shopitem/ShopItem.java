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
import java.util.stream.Stream;

import static io.pillopl.eventsource.domain.shopitem.ShopItemState.*;
import static java.util.stream.Collectors.toList;

@Value
public class ShopItem {

  @Getter
  private final UUID uuid;
  private final Stream.Builder<DomainEvent> changes;
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

  public ShopItem apply(ItemBought event) {
    return new ShopItem(event.getUuid(), changes, BOUGHT);
  }

  public ShopItem apply(ItemPaid event) {
    return new ShopItem(event.getUuid(), changes, PAID);
  }

  public ShopItem apply(ItemPaymentTimeout event) {
    return new ShopItem(event.getUuid(), changes, PAYMENT_MISSING);
  }

  public static ShopItem from(UUID uuid, Stream<DomainEvent> history) {
    return history
      .reduce(
        new ShopItem(uuid, Stream.builder(), INITIALIZED),
        ShopItem::apply,
        (t1, t2) -> {
          throw new UnsupportedOperationException();
        }
      );
  }

  private ShopItem apply(DomainEvent event) {
    try {
      return new MethodInvoker().invoke(this, event);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(String.format("Cannot handle event %s", event.getClass()));
    }
  }

  private ShopItem applyChange(DomainEvent event) {
    final ShopItem item = apply(event);
    return new ShopItem(item.getUuid(), item.changes.add(event), item.getState());
  }

  public List<DomainEvent> getUncommittedChanges() {
    return changes.build().collect(toList());
  }

  public ShopItem markChangesAsCommitted() {
    return new ShopItem(uuid, Stream.builder(), state);
  }

}
