package com.github.alien11689.messagenbrokers.amqp.topic

import com.github.alien11689.messagenbrokers.helper.Docker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import spock.lang.AutoCleanup
import spock.lang.Requires
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY

@Requires({ Docker.isRunning('rmqwithscheduler') })
class FanoutTest extends Specification {
    @AutoCleanup(quiet = true)
    Connection connection = RMQ_CONNECTION_FACTORY.newConnection()

    @AutoCleanup(quiet = true)
    Channel channel = connection.createChannel()

    def 'should send message to fanout and receive it'() {
        given:
            channel.exchangeDeclare('exchange_for_fanout', 'fanout')
            String queue1 = channel.queueDeclare().queue
            channel.queueBind(queue1, 'exchange_for_fanout', '???')
            List<String> messages1 = []
            channel.basicConsume(queue1, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    messages1 << new String(body, 'UTF-8')
                }
            })
            String queue2 = channel.queueDeclare().queue
            channel.queueBind(queue2, 'exchange_for_fanout', '!!!!')
            List<String> messages2 = []
            channel.basicConsume(queue2, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    messages2 << new String(body, 'UTF-8')
                }
            })
            String messageText = UUID.randomUUID().toString()
        when:
            channel.basicPublish('exchange_for_fanout', '&&&', null, messageText.getBytes('UTF-8'))
        then:
            new PollingConditions(timeout: 10).eventually {
                messageText in messages1
                messageText in messages2
            }
    }
}
