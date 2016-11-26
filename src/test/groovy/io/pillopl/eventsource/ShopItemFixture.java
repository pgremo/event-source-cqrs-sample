package io.pillopl.eventsource;


import io.pillopl.eventsource.domain.shopitem.ShopItem;

import java.time.Instant;
import java.util.UUID;

import static io.pillopl.eventsource.domain.shopitem.ShopItemState.INITIALIZED;
import static java.time.Instant.now;
import static java.util.Collections.emptyList;

public class ShopItemFixture {

  public static final Instant ANY_TIME = now();
  public static final int ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT = 48;


  public static ShopItem initialized() {
    return new ShopItem(null, emptyList(), INITIALIZED);
  }

  public static ShopItem bought(UUID uuid) {
    return initialized()
      .buy(uuid, ANY_TIME, ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT)
      .markChangesAsCommitted();
  }

  public static ShopItem paid(UUID uuid) {
    return initialized()
      .buy(uuid, now(), ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT)
      .pay(now())
      .markChangesAsCommitted();
  }

  public static ShopItem withTimeout(UUID uuid) {
    return initialized()
      .buy(uuid, now(), ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT)
      .markTimeout(now())
      .markChangesAsCommitted();
  }

  public static ShopItem withTimeoutAndPaid(UUID uuid) {
    return initialized()
      .buy(uuid, now(), ANY_NUMBER_OF_HOURS_TO_PAYMENT_TIMEOUT)
      .markTimeout(now())
      .pay(now())
      .markChangesAsCommitted();
  }

}
