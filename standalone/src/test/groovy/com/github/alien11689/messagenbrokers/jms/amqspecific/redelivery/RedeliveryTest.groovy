package com.github.alien11689.messagenbrokers.jms.amqspecific.redelivery

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.RedeliveryPolicy
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.atomic.AtomicInteger

class RedeliveryTest extends Specification {
    ConnectionFactory amq = new ActiveMQConnectionFactory('admin', 'admin', 'tcp://localhost:61616')
    AtomicInteger messageAmount = new AtomicInteger(0)
    Connection connection

    def setup() {
        (amq as ActiveMQConnectionFactory).redeliveryPolicy = new RedeliveryPolicy(
            maximumRedeliveries: 0
        )
        connection = amq.createConnection()
    }

    def 'should redelivery 1 time'() {
        given:
            String messageText = UUID.randomUUID().toString()
            failMessage('FOO', messageText)
        when:
            sendMessage('FOO', messageText)
        then:
            new PollingConditions(timeout: 20, delay: 1).eventually {
                messageAmount.get() == 2
            }
            readMessage('DLQ.FOO') == messageText
        cleanup:
            connection.close()
    }

    private void failMessage(String queue, String text) {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        consumer.messageListener = new MessageListener() {
            @Override
            void onMessage(Message message) {
                String messageText = (message as TextMessage).text
                println message.properties
                if (messageText == text) {
                    println message.getIntProperty('JMSXDeliveryCount')
                    println "Increment and get ${messageAmount.incrementAndGet()}"
                    throw new RuntimeException('Rejected')
                }
            }
        }
        connection.start()
    }

    private void sendMessage(String queue, String messageText) {
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createQueue(queue))
        messageProducer.send(session.createTextMessage(messageText))
    }

    private String readMessage(String queue) {
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(10000) as TextMessage
        return message.text
    }
}