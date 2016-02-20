package com.github.alien11689.messagenbrokers.amqp.queue

import com.github.alien11689.messagenbrokers.helper.Docker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import groovy.util.logging.Slf4j
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.TimeoutException

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY

@Slf4j
@Requires({ Docker.isRunning('rmqwithscheduler') })
class SendAndListenTest extends Specification {
    @Shared
    String messageText = UUID.randomUUID().toString()

    def 'should send message'() {
        when:
            Connection connection = null
            Channel channel = null
            try {
                connection = RMQ_CONNECTION_FACTORY.newConnection()
                channel = connection.createChannel()
                channel.queueDeclare("simple.send.listen", true, false, false, null)
                channel.basicPublish("", "simple.send.listen", null, messageText.getBytes("UTF-8"))
            } catch (IOException | TimeoutException e) {
                log.error("Exception occured", e)
                throw new RuntimeException(e)
            } finally {
                if (channel != null) {
                    try {
                        channel.close()
                    } catch (IOException | TimeoutException e) {
                        log.error("Cannot close producer", e)
                        throw new RuntimeException(e)
                    }
                }
                if (connection != null) {
                    try {
                        connection.close()
                    } catch (IOException e) {
                        log.error("Cannot close connection", e)
                        throw new RuntimeException(e)
                    }
                }
            }
        then:
            noExceptionThrown()
    }

    def 'should listen on message'() {
        given:
            List<String> receivedMessages = []
            Connection connection = RMQ_CONNECTION_FACTORY.newConnection()
            Channel channel = connection.createChannel()
            channel.queueDeclare("simple.send.listen", true, false, false, null)
        when:
            channel.basicConsume("simple.send.listen", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String text = new String(body, "UTF-8");
                    receivedMessages.add(text);
                }
            })
        then:
            new PollingConditions(timeout: 5).eventually {
                messageText in receivedMessages
            }
        cleanup:
            channel?.close()
            connection?.close()
    }
}
