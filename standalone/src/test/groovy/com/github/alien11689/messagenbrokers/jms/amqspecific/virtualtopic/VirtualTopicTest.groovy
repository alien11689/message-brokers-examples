package com.github.alien11689.messagenbrokers.jms.amqspecific.virtualtopic

import org.apache.activemq.ActiveMQConnectionFactory
import spock.lang.Specification

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.atomic.AtomicInteger

class VirtualTopicTest extends Specification {
    ConnectionFactory amq = new ActiveMQConnectionFactory('admin', 'admin', 'tcp://localhost:61616')
    AtomicInteger messageAmount = new AtomicInteger(0)
    Connection connection = amq.createConnection()

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
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createTopic(topic))
        messageProducer.send(session.createTextMessage(messageText))
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