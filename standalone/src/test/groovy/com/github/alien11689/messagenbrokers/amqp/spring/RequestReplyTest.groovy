package com.github.alien11689.messagenbrokers.amqp.spring

import com.github.alien11689.messagenbrokers.helper.Docker
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
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
            rabbitTemplate.send('spring.rr.in', new Message(message.getBytes('UTF-8'), new MessageProperties()))
        then:
            new PollingConditions(timeout: 5).eventually {
                Message receive = rabbitTemplate.receive('spring.rr.out')
                new String(receive.body, 'UTF-8') == "Spring: $message" as String
            }
    }
}
