package com.github.alien11689.messagenbrokers.jms.amqspecific.network

import com.github.alien11689.messagenbrokers.helper.Docker
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY
import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY2

@Requires({ Docker.isRunning('amqNetwork1', 'amqNetwork2') })
class NetworkOfBrokersTest extends Specification {
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

    private static void sendMessageAmq1(String queue, String messageText) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(session.createQueue(queue))
        messageProducer.send(session.createTextMessage(messageText))
    }

    private static String readMessageAmq2(String queue) {
        Connection connection = AMQ_CONNECTION_FACTORY2.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(10000) as TextMessage
        return message.text
    }
}