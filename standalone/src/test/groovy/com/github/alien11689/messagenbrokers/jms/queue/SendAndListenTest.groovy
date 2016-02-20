package com.github.alien11689.messagenbrokers.jms.queue

import com.github.alien11689.messagenbrokers.helper.Docker
import groovy.util.logging.Slf4j
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Slf4j
@Requires({ Docker.isRunning('justAmq') })
@Stepwise
class SendAndListenTest extends Specification {
    @Shared
    String messageText = UUID.randomUUID().toString()

    def 'should send message'() {
        given:
            Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            MessageProducer producer = session.createProducer(session.createQueue("simple.send.listen"))
            TextMessage message = session.createTextMessage(messageText)
            message.setIntProperty("iteration", 2)
        when:
            producer.send(message)
        then:
            noExceptionThrown()
        cleanup:
            producer?.close()
            session?.close()
            connection?.close()
    }

    def 'should listen on message'() {
        given:
            List<String> receivedMessages = []
            Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            MessageConsumer consumer = session.createConsumer(session.createQueue("simple.send.listen"))
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = message as TextMessage
                        try {
                            String text = textMessage.getText()
                            receivedMessages << text
                        } catch (JMSException e) {
                            log.error("Cannot read text from message", e)
                            throw new RuntimeException(e)
                        }
                    } else {
                        throw new RuntimeException("No message")
                    }

                }
            })
        when:
            connection.start()
        then:
            new PollingConditions(timeout: 5).eventually {
                messageText in receivedMessages
            }
        cleanup:
            consumer?.close()
            session?.close()
            connection?.close()
    }
}
