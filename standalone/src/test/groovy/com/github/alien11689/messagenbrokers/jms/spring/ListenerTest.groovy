package com.github.alien11689.messagenbrokers.jms.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session
import javax.jms.TextMessage

@ContextConfiguration(classes = Config)
class ListenerTest extends Specification {

    @Autowired
    JmsTemplate jmsTemplate

    def 'should send message to listener and receive message on another queue'() {
        given:
            String message = UUID.randomUUID().toString()
        when:
            jmsTemplate.send('spring.in', new MessageCreator() {
                @Override
                Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(message)
                }
            })
        then:
            TextMessage receivedMessage = jmsTemplate.receive('spring.out') as TextMessage
            receivedMessage.text == "Spring: $message"
    }
}
