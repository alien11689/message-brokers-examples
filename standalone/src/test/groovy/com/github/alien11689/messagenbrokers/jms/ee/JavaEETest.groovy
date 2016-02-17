package com.github.alien11689.messagenbrokers.jms.ee

import groovyx.net.http.HTTPBuilder
import org.apache.activemq.ActiveMQConnectionFactory
import spock.lang.Specification

import javax.jms.Connection
import javax.jms.ConnectionFactory
import javax.jms.MessageConsumer
import javax.jms.Session
import javax.jms.TextMessage

class JavaEETest extends Specification {
    ConnectionFactory amq = new ActiveMQConnectionFactory('admin', 'admin', 'tcp://localhost:61616')
    Connection connection = amq.createConnection()

    def 'should get message from virtual topic'() {
        given:
            String messageText = UUID.randomUUID().toString()
        when:
            new HTTPBuilder('http://localhost:8080/ee/message').post(body: messageText)
        then:
            readMessage('tomee.out') == messageText
        cleanup:
            connection.close()
    }

    private String readMessage(String queue) {
        Connection connection = amq.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(1000) as TextMessage
        return message?.text
    }
}