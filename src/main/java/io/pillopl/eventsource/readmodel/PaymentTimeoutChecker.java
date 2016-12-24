package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.boundary.ShopItems;
import io.pillopl.eventsource.domain.shopitem.commands.MarkPaymentTimeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;

import static java.sql.Timestamp.from;
import static java.util.UUID.fromString;

@Component
@Slf4j
class PaymentTimeoutChecker {

  private static final String ITEMS_TIMEOUT_SQL_QUERY = "SELECT uuid FROM items WHERE when_payment_timeout <= ? AND status = 'BOUGHT'";

  private final ShopItems shopItems;
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  PaymentTimeoutChecker(ShopItems shopItems, JdbcTemplate jdbcTemplate) {
    this.shopItems = shopItems;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60)
  @Transactional
  void checkPaymentTimeouts() {
    final Instant now = Instant.now();
    jdbcTemplate.query(
      ITEMS_TIMEOUT_SQL_QUERY,
      rs -> {
        String uuid = rs.getString("uuid");
        log.info("Marking payment {} that did not arrive at {}", uuid, now);
        shopItems.markPaymentTimeout(new MarkPaymentTimeout(fromString(uuid), now));
      },
      from(now));
  }
}
