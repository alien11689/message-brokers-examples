package com.github.alien11689.messagenbrokers.camel

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.jms.BytesMessage
import javax.jms.MessageConsumer
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.Executors

class CamelRouteTest extends Specification {
    ConnectionFactory rmqConnectionFactory = new ConnectionFactory().with {
        it
        it.host = 'Localhost'
        it.username = 'admin'
        it.password = 'admin'
        it
    }

    @AutoCleanup(quiet = true)
    Connection rmqConnection = rmqConnectionFactory.newConnection()

    @AutoCleanup(quiet = true)
    Channel channel = rmqConnection.createChannel()

    javax.jms.ConnectionFactory amqConnectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616")

    @AutoCleanup(quiet = true)
    javax.jms.Connection amqConnection = amqConnectionFactory.createConnection()

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
            TextMessage textMessage = consumer.receive(10000) as BytesMessage
            textMessage?.text == message
    }
}
