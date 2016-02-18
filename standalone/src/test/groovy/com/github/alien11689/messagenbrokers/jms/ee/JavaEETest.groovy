package com.github.alien11689.messagenbrokers.jms.ee

import spock.lang.Specification

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class JavaEETest extends Specification {
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessage('tomee.in', messageText)
        then:
            readMessage('tomee.out') == "Tomee: $messageText"
        cleanup:
            connection.close()
    }

    private static void sendMessage(String queue, String messageText) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createQueue(queue))
        messageProducer.send(session.createTextMessage(messageText))
    }

    private static String readMessage(String queue) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(1000) as TextMessage
        return message?.text
    }
}