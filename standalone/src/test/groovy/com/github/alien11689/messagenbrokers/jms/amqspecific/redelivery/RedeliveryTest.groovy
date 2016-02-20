package com.github.alien11689.messagenbrokers.jms.amqspecific.redelivery

import com.github.alien11689.messagenbrokers.helper.Docker
import com.github.alien11689.messagenbrokers.jms.JmsSpockSpecification
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.RedeliveryPolicy
import spock.lang.Requires
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage
import java.util.concurrent.atomic.AtomicInteger

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Requires({ Docker.isRunning('amqWithRedeliveryAndScheduler') })
class RedeliveryTest extends JmsSpockSpecification {
    AtomicInteger messageAmount = new AtomicInteger(0)
    Connection connection

    def setup() {
        (AMQ_CONNECTION_FACTORY as ActiveMQConnectionFactory).redeliveryPolicy = new RedeliveryPolicy(
            maximumRedeliveries: 0
        )
        connection = AMQ_CONNECTION_FACTORY.createConnection()
    }

    def 'should redelivery 1 time'() {
        given:
            String messageText = UUID.randomUUID().toString()
            failMessage('FOO', messageText)
        when:
            sendMessageQueue('FOO', messageText)
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
}