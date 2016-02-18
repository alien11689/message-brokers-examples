package com.github.alien11689.messagenbrokers.jms.amqspecific.virtualtopic

import com.github.alien11689.messagenbrokers.jms.JmsSpockSpecification

import javax.jms.Connection

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class VirtualTopicTest extends JmsSpockSpecification {
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessageTopic('VirtualTopic.FOO', messageText)
        then:
            readMessage('Consumer.client1.VirtualTopic.FOO') == messageText
            readMessage('Consumer.client2.VirtualTopic.FOO') == messageText
        cleanup:
            connection.close()
    }
}