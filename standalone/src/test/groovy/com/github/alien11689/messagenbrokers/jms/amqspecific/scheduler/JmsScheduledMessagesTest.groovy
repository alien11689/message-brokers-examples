package com.github.alien11689.messagenbrokers.jms.amqspecific.scheduler

import com.github.alien11689.messagenbrokers.helper.Docker
import org.apache.activemq.ScheduledMessage
import spock.lang.AutoCleanup
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.Destination
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.MessageProducer
import javax.jms.Queue
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Requires({ Docker.isRunning('amqWithScheduler') })
class JmsScheduledMessagesTest extends Specification {

    String consumerText

    @Unroll
    def 'should send message with 8 second delay to queue #currentUuid'() {
        given:
            String queue = "scheduledMessageQueue-$currentUuid"
            Destination destination = session.createQueue(queue)
            prepareConsumer(destination)
            MessageProducer producer = prepareProducer(destination)
            TextMessage message = prepareMessage("Message: $currentUuid")
            println("Send time: ${new Date()}")
        when:
            producer.send(message)
        then:
            Thread.sleep(5000)
        and:
            consumerText == null
        and:
            new PollingConditions().within(5) {
                consumerText != null
            }
            Thread.sleep(1000)
        where:
            currentUuid << [UUID.randomUUID()]
    }

    private TextMessage prepareMessage(msg) {
        Message message = session.createTextMessage(msg)
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 8000)
        return message
    }

    private MessageProducer prepareProducer(Queue destination) {
        return session.createProducer(destination)
    }

    private void prepareConsumer(Queue destination) {
        MessageConsumer consumer = session.createConsumer(destination)
        consumer.messageListener = listener
        connection.start()
    }

    MessageListener listener = new MessageListener() {
        @Override
        void onMessage(Message message) {
            println("Message received at ${new Date()}")
            consumerText = (message as TextMessage).text
            message.acknowledge()
        }
    }

    @AutoCleanup(quiet = true)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    @AutoCleanup(quiet = true)
    Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE)
}
