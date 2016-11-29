package io.pillopl.eventsource;

import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;
import io.pillopl.eventsource.readmodel.ReadModelOnDomainEventUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.MethodInvokingMessageHandler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@Configuration
@IntegrationComponentScan
public class Application {

  @Autowired ReadModelOnDomainEventUpdater readModelUpdater;

  @Bean
  public IntegrationFlow eventBusOutFlow() {
    MethodInvokingMessageHandler handler = new MethodInvokingMessageHandler(readModelUpdater, "handle");
    return IntegrationFlows.from("events")
      .<Object, Class<?>>route(Object::getClass, m -> m
        .subFlowMapping(ItemBought.class.getName(), sf -> sf.handle(handler))
        .subFlowMapping(ItemPaymentTimeout.class.getName(), sf -> sf.handle(handler))
        .subFlowMapping(ItemPaid.class.getName(), sf -> sf.handle(handler))
        .get())
      .get();
  }

  public static void main(String[] args) {
    new SpringApplication(Application.class).run(args);
  }
}
