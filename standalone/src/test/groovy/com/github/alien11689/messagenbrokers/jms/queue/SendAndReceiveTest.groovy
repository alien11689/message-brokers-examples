package com.github.alien11689.messagenbrokers.jms.queue

import com.github.alien11689.messagenbrokers.helper.Docker
import groovy.util.logging.Slf4j
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.jms.Connection
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Slf4j
@Requires({ Docker.isRunning('justAmq') })
@Stepwise
class SendAndReceiveTest extends Specification {
    @Shared
    String messageText = UUID.randomUUID().toString()

    def 'should send message'() {
        when:
            Connection connection = null
            Session session = null
            MessageProducer producer = null
            try {
                connection = AMQ_CONNECTION_FACTORY.createConnection()
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
                producer = session.createProducer(session.createQueue('simple.send.receive'))
                TextMessage message = session.createTextMessage(messageText)
                message.setIntProperty('iteration', 2)
                producer.send(message)
            } catch (JMSException e) {
                log.error('Exception occured', e)
                throw new RuntimeException(e)
            } finally {
                if (producer != null) {
                    try {
                        producer.close()
                    } catch (JMSException e) {
                        log.error('Cannot close producer', e)
                        throw new RuntimeException(e)
                    }
                }
                if (session != null) {
                    try {
                        session.close()
                    } catch (JMSException e) {
                        log.error('Cannot close session', e)
                        throw new RuntimeException(e)
                    }
                }
                if (connection != null) {
                    try {
                        connection.close()
                    } catch (JMSException e) {
                        log.error('Cannot close connection', e)
                        throw new RuntimeException(e)
                    }
                }
            }
        then:
            noExceptionThrown()
    }

    def 'should receive message'() {
        when:
            Connection connection = null
            Session session = null
            MessageConsumer consumer = null
            List<String> receivedMessages = []
            try {
                connection = AMQ_CONNECTION_FACTORY.createConnection()
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
                consumer = session.createConsumer(session.createQueue('simple.send.receive'))
                connection.start()
                Message message = consumer.receive(60000)
                if (message instanceof TextMessage) {
                    TextMessage textMessage = message as TextMessage
                    receivedMessages << textMessage.text
                } else {
                    throw new RuntimeException('No message')
                }
            } catch (JMSException e) {
                log.error('Exception occured', e)
                throw new RuntimeException(e)
            } finally {
                if (consumer != null) {
                    try {
                        consumer.close()
                    } catch (JMSException e) {
                        log.error('Cannot close producer', e)
                        throw new RuntimeException(e)
                    }
                }
                if (session != null) {
                    try {
                        session.close()
                    } catch (JMSException e) {
                        log.error('Cannot close session', e)
                        throw new RuntimeException(e)
                    }
                }
                if (connection != null) {
                    try {
                        connection.close()
                    } catch (JMSException e) {
                        log.error('Cannot close connection', e)
                        throw new RuntimeException(e)
                    }
                }
            }
        then:
            messageText in receivedMessages
    }
}
