package io.pillopl.eventsource.integration

import groovy.transform.CompileStatic
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@SpringBootTest
@CompileStatic
@WebAppConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ActiveProfiles("test")
class IntegrationSpec extends Specification {
}
