package io.pillopl.eventsource.boundary;

import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.Repository;
import io.pillopl.eventsource.domain.shopitem.commands.Buy;
import io.pillopl.eventsource.domain.shopitem.commands.MarkPaymentTimeout;
import io.pillopl.eventsource.domain.shopitem.commands.Pay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.UnaryOperator;

@Service
@Transactional
@Slf4j
public class ShopItems {

  private final Repository<ShopItem, UUID> itemRepository;
  private final int hoursToPaymentTimeout;

  @Autowired
  public ShopItems(Repository<ShopItem, UUID> itemRepository, @Value("${hours.to.payment.timeout:48}") int hoursToPaymentTimeout) {
    this.itemRepository = itemRepository;
    this.hoursToPaymentTimeout = hoursToPaymentTimeout;
  }

  public void buy(Buy command) {
    withItem(command.getUuid(), tx ->
      tx.buy(command.getUuid(), command.getWhen(), hoursToPaymentTimeout)
    );
    log.info("{} item bought at {}", command.getUuid(), command.getWhen());
  }

  public void pay(Pay command) {
    withItem(command.getUuid(), tx ->
      tx.pay(command.getWhen())
    );
    log.info("{} item paid at {}", command.getUuid(), command.getWhen());
  }

  public void markPaymentTimeout(MarkPaymentTimeout command) {
    withItem(command.getUuid(), tx ->
      tx.markTimeout(command.getWhen())
    );
    log.info("{} item marked as payment timeout at {}", command.getUuid(), command.getWhen());
  }

  public ShopItem getByUUID(UUID uuid) {
    return itemRepository.load(uuid);
  }

  private ShopItem withItem(UUID uuid, UnaryOperator<ShopItem> action) {
    ShopItem tx = getByUUID(uuid);
    ShopItem modified = action.apply(tx);
    return itemRepository.save(modified);
  }

}
