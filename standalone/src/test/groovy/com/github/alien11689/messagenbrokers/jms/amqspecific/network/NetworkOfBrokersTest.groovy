package com.github.alien11689.messagenbrokers.jms.amqspecific.network

import org.apache.activemq.ActiveMQConnectionFactory
import spock.lang.Specification
import spock.lang.Unroll

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

class NetworkOfBrokersTest extends Specification {
    ConnectionFactory amq1 = new ActiveMQConnectionFactory('admin', 'admin', 'tcp://localhost:61616')
    ConnectionFactory amq2 = new ActiveMQConnectionFactory('admin', 'admin', 'tcp://localhost:61617')

    @Unroll
    def 'should send message to amq1 and receive on amq2'() {
        given:
            String messageText = 'Hello from amq1'
        when:
            sendMessageAmq1(queue, messageText)
        then:
            readMessageAmq2(queue) == messageText
        where:
            queue << ['TEST.FOO', 'TEST.BAR']

    }

    private void sendMessageAmq1(String queue, String messageText) {
        Connection connection = amq1.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createQueue(queue))
        messageProducer.send(session.createTextMessage(messageText))
    }

    private String readMessageAmq2(String queue){
        Connection connection = amq2.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(10000) as TextMessage
        return message.text
    }
}