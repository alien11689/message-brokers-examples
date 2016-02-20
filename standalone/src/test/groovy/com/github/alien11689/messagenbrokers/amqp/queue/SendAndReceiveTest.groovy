package com.github.alien11689.messagenbrokers.amqp.queue

import com.github.alien11689.messagenbrokers.helper.Docker
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.GetResponse
import groovy.util.logging.Slf4j
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeoutException

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY

@Slf4j
@Requires({ Docker.isRunning('rmqwithscheduler') })
class SendAndReceiveTest extends Specification {
    @Shared
    String messageText = UUID.randomUUID().toString()

    def 'should send message'() {
        when:
            Connection connection = null
            Channel channel = null
            try {
                connection = RMQ_CONNECTION_FACTORY.newConnection()
                channel = connection.createChannel()
                channel.queueDeclare("simple.send.receive", true, false, false, null)
                channel.basicPublish("", "simple.send.receive", null, messageText.getBytes("UTF-8"))
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

    def 'should receive message'() {
        when:
            List<String> receivedMessages = []
            Connection connection = null
            Channel channel = null
            try {
                connection = RMQ_CONNECTION_FACTORY.newConnection()
                channel = connection.createChannel()
                channel.queueDeclare("simple.send.receive", true, false, false, null)
                GetResponse getResponse = channel.basicGet("simple.send.receive", true)
                receivedMessages << new String(getResponse.getBody(), "UTF-8")
            } catch (IOException | TimeoutException e) {
                log.error("Exception occured", e)
            } finally {
                if (channel != null) {
                    try {
                        channel.close()
                    } catch (IOException | TimeoutException e) {
                        log.error("Cannot close producer", e)
                    }
                }
                if (connection != null) {
                    try {
                        connection.close()
                    } catch (IOException e) {
                        log.error("Cannot close connection", e)
                    }
                }
            }
        then:
            messageText in receivedMessages
    }
}
