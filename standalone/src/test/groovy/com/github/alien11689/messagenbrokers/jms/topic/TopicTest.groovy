package com.github.alien11689.messagenbrokers.jms.topic

import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class TopicTest extends Specification {
    @AutoCleanup(quiet = true)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    @AutoCleanup(quiet = true)
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    @AutoCleanup(quiet = true)
    MessageConsumer consumer = session.createConsumer(session.createTopic("simple.tpc.send"))

    @AutoCleanup(quiet = true)
    MessageProducer producer = session.createProducer(session.createTopic("simple.tpc.send"))

    def setup() {
        connection.start()
    }

    def 'should send message to topic and receive it'() {
        given:
            List<String> messages = []
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = message as TextMessage
                    messages << textMessage.text

                }
            })
            String messageText = UUID.randomUUID().toString()
        when:
            producer.send(session.createTextMessage(messageText))
        then:
            new PollingConditions(timeout: 10000).eventually {
                messageText in messages
            }
    }
}
