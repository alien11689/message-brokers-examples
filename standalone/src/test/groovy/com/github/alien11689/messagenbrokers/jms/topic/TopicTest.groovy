package com.github.alien11689.messagenbrokers.jms.topic

import com.github.alien11689.messagenbrokers.helper.Docker
import com.github.alien11689.messagenbrokers.jms.JmsSpockSpecification
import spock.lang.AutoCleanup
import spock.lang.Requires
import spock.util.concurrent.PollingConditions

import javax.jms.Connection
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Requires({ Docker.isRunning('justAmq') })
class TopicTest extends JmsSpockSpecification {
    @AutoCleanup(quiet = true)
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    @AutoCleanup(quiet = true)
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

    @AutoCleanup(quiet = true)
    MessageConsumer consumer1 = session.createConsumer(session.createTopic('simple.tpc.send'))

    @AutoCleanup(quiet = true)
    MessageConsumer consumer2 = session.createConsumer(session.createTopic('simple.tpc.>'))

    def setup() {
        connection.start()
    }

    def 'should send message to topic and receive it'() {
        given:
            List<String> messages1 = []
            consumer1.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = message as TextMessage
                    messages1 << textMessage.text

                }
            })
            List<String> messages2 = []
            consumer2.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = message as TextMessage
                    messages2 << textMessage.text

                }
            })
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessageTopic('simple.tpc.send', messageText)
        then:
            new PollingConditions(timeout: 10).eventually {
                messageText in messages1
                messageText in messages2
            }
    }
}
