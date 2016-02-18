package com.github.alien11689.messagenbrokers.amqp.requestreply

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.QueueingConsumer
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.util.concurrent.Executors

class RequestReplyTest extends Specification {
    ConnectionFactory connectionFactory = new ConnectionFactory(
        host: 'localhost',
        username: 'admin',
        password: 'admin'
    )

    @AutoCleanup(quiet = true)
    Connection connection = connectionFactory.newConnection()

    @AutoCleanup(quiet = true)
    Channel channel = connection.createChannel()

    String replyQueue = 'responseQueue'

    def 'should reply to request'() {
        given:
            Executors.newSingleThreadExecutor().submit(new Calculator())
            channel.queueDeclare(replyQueue, true, false, false, null)
            String correlationId = UUID.randomUUID().toString()
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .replyTo(replyQueue)
                .correlationId(correlationId)
                .build()
            channel.queueDeclare('simple.adder', true, false, false, null)
            QueueingConsumer queueingConsumer = new QueueingConsumer(channel)
            channel.basicConsume(replyQueue, true, queueingConsumer)
        when:
            channel.basicPublish('', 'simple.adder', basicProperties, '4,7'.getBytes('UTF-8'))

        then:
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery(60000)
            delivery.properties.correlationId == correlationId
            new String(delivery.body, 'UTF-8') == '11'
    }
}
