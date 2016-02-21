package com.github.alien11689.messagenbrokers.amqp.spring

import com.github.alien11689.messagenbrokers.helper.Docker
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Requires
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@Requires({ Docker.isRunning('rmqwithscheduler') })
@ContextConfiguration(classes = Config)
class RequestReplyTest extends Specification {
    @Autowired
    RabbitTemplate rabbitTemplate

    def 'should send message to listener and receive message on another queue'() {
        given:
            String message = UUID.randomUUID().toString()
        when:
            rabbitTemplate.convertAndSend('spring.rr.in', message)
        then:
            new PollingConditions(timeout: 5).eventually {
                String receivedMessage = rabbitTemplate.receiveAndConvert('spring.rr.out')
                receivedMessage == "Spring: $message" as String
            }
    }
}
