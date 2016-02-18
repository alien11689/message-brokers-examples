package com.github.alien11689.messagenbrokers.amqp.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY

class TopicTest extends Specification {
    @AutoCleanup(quiet = true)
    Connection connection = RMQ_CONNECTION_FACTORY.newConnection()

    @AutoCleanup(quiet = true)
    Channel channel = connection.createChannel()

    def 'should send message to topic and receive it'() {
        given:
            channel.exchangeDeclare("exchange_for_topic", "topic")
            String queue = channel.queueDeclare().queue
            channel.queueBind(queue, "exchange_for_topic", "simple.tpc.send")
            List<String> messages = []
            channel.basicConsume(queue, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    messages << new String(body, "UTF-8")
                }
            })
            String messageText = UUID.randomUUID().toString()
        when:
            channel.basicPublish("exchange_for_topic", "simple.tpc.send", null, messageText.getBytes("UTF-8"));
        then:
            new PollingConditions(timeout: 10000).eventually {
                messageText in messages
            }
    }
}
