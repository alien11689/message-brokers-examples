package com.github.alien11689.messagenbrokers.jms.spring

import com.github.alien11689.messagenbrokers.helper.Docker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.test.context.ContextConfiguration
import spock.lang.Requires
import spock.lang.Specification

import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session
import javax.jms.TextMessage

@Requires({ Docker.isRunning('justAmq') })
@ContextConfiguration(classes = Config)
class RequestReplyTest extends Specification {

    @Autowired
    JmsTemplate jmsTemplate

    def 'should send message to listener and receive message on another queue'() {
        given:
            String message = UUID.randomUUID().toString()
        when:
            jmsTemplate.send('spring.rr.in', new MessageCreator() {
                @Override
                Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(message)
                }
            })
        then:
            TextMessage receivedMessage = jmsTemplate.receive('spring.rr.out') as TextMessage
            receivedMessage.text == "Spring: $message"
    }
}
