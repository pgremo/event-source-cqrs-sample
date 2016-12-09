package io.pillopl.eventsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@Configuration
@IntegrationComponentScan
public class Application {

  @Bean
  public IntegrationFlow split() {
    return IntegrationFlows.from("events-out")
      .split()
      .channel("events-in")
      .get();
  }

  public static void main(String... args) {
    new SpringApplication(Application.class).run(args);
  }
}
