package com.github.alien11689.messagenbrokers.jms.amqspecific.compositedestination

import com.github.alien11689.messagenbrokers.jms.JmsSpockSpecification

import javax.jms.Connection
import javax.jms.Message

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class CompositeDestinationTest extends JmsSpockSpecification {
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessageTopic('notifications', messageText, { Message m -> m.setStringProperty('notificationType', 'SMS') })
        then:
            Thread.sleep(1000)
            readMessage('notifications.sms') == messageText
            readMessage('notifications.email') == null
            readMessage('notifications.all') == messageText
        cleanup:
            connection.close()
    }
}