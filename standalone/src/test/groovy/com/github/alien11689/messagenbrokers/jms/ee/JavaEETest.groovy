package com.github.alien11689.messagenbrokers.jms.ee

import com.github.alien11689.messagenbrokers.jms.JmsSpockSpecification

import javax.jms.Connection

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

class JavaEETest extends JmsSpockSpecification {
    Connection connection = AMQ_CONNECTION_FACTORY.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            sendMessageQueue('tomee.in', messageText)
        then:
            readMessage('tomee.out') == "Tomee: $messageText"
        cleanup:
            connection.close()
    }
}