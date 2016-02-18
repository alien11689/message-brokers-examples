package com.github.alien11689.messagenbrokers.jms.amqspecific.compositedestination

import spock.lang.Specification

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.atomic.AtomicInteger

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class CompositeDestinationTest extends Specification {
    AtomicInteger messageAmount = new AtomicInteger(0)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessage('notifications', messageText, 'SMS')
        then:
            Thread.sleep(1000)
            readMessage('notifications.sms') == messageText
            readMessage('notifications.email') == null
            readMessage('notifications.all') == messageText
        cleanup:
            connection.close()
    }

    private void sendMessage(String topic, String messageText, String notificationType) {
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createTopic(topic))
        TextMessage message = session.createTextMessage(messageText)
        message.setStringProperty('notificationType', notificationType)
        messageProducer.send(message)
    }

    private String readMessage(String queue) {
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(1000) as TextMessage
        return message?.text
    }
}