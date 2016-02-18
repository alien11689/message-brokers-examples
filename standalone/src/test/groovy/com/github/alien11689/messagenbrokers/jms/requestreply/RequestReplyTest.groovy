package com.github.alien11689.messagenbrokers.jms.requestreply

import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.jms.Connection
import javax.jms.MapMessage
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TemporaryQueue
import java.util.concurrent.Executors

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class RequestReplyTest extends Specification {
    @AutoCleanup(quiet = true)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    @AutoCleanup(quiet = true)
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    @AutoCleanup(quiet = true)
    MessageProducer producer = session.createProducer(session.createQueue("simple.adder"))

    TemporaryQueue temporaryQueue = session.createTemporaryQueue()

    @AutoCleanup(quiet = true)
    MessageConsumer consumer = session.createConsumer(temporaryQueue)

    def setup() {
        connection.start()
    }

    def 'should reply to request'() {
        given:
            Executors.newSingleThreadExecutor().submit(new Calculator())
            MapMessage message = session.createMapMessage()
            message.setInt("a", 5)
            message.setInt("b", 10)
            message.setJMSReplyTo(temporaryQueue)
        when:
            producer.send(message)
        then:
            MapMessage mapMessage = (MapMessage) consumer.receive(5000) as MapMessage
            mapMessage.getInt("result") == 15

    }
}
