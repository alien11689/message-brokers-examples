package com.github.alien11689.messagenbrokers.camel

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.jms.MessageConsumer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.Executors

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY
import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class CamelRouteTest extends Specification {

    @AutoCleanup(quiet = true)
    Connection rmqConnection = RMQ_CONNECTION_FACTORY.newConnection()

    @AutoCleanup(quiet = true)
    Channel channel = rmqConnection.createChannel()

    @AutoCleanup(quiet = true)
    javax.jms.Connection amqConnection = AMQ_CONNECTION_FACTORY.createConnection()

    @AutoCleanup(quiet = true)
    Session session = amqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    @AutoCleanup(quiet = true)
    MessageConsumer consumer = session.createConsumer(session.createQueue('fromCamel'))

    def setup() {
        amqConnection.start()
    }

    def 'should move message from rmq to amq'() {
        given:
            String message = UUID.randomUUID().toString()
            channel.queueDeclare('forCamel', true, false, false, null)
            channel.exchangeDeclare('tasks', 'direct', true)
            channel.queueBind('forCamel', 'tasks', 'camel')
            Executors.newSingleThreadExecutor().submit(new CamelRoute())
        when:
            channel.basicPublish('tasks', 'camel', null, message.getBytes('UTF-8'))
        then:
            TextMessage textMessage = consumer.receive(10000) as TextMessage
            textMessage?.text == message
    }
}
