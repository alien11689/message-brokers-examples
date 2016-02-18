package com.github.alien11689.messagenbrokers.jms.amqspecific.virtualtopic

import spock.lang.Specification

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.atomic.AtomicInteger

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class VirtualTopicTest extends Specification {
    AtomicInteger messageAmount = new AtomicInteger(0)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessage('VirtualTopic.FOO', messageText)
        then:
            readMessage('Consumer.client1.VirtualTopic.FOO') == messageText
            readMessage('Consumer.client2.VirtualTopic.FOO') == messageText
        cleanup:
            connection.close()
    }

    private void sendMessage(String topic, String messageText) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createTopic(topic))
        messageProducer.send(session.createTextMessage(messageText))
    }

    private String readMessage(String queue) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(1000) as TextMessage
        return message?.text
    }
}