package com.github.alien11689.messagenbrokers.jms

import spock.lang.Specification

import javax.jms.Connection
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

abstract class JmsSpockSpecification extends Specification {
    void sendMessageQueue(String destination, String messageText, Closure propertyClosure = { Message message -> }) {
        sendMessage(false, destination, messageText, propertyClosure)
    }

    void sendMessageTopic(String destination, String messageText, Closure propertyClosure = { Message message -> }) {
        sendMessage(true, destination, messageText, propertyClosure)
    }

    private void sendMessage(boolean topic, String destination, String messageText, Closure propertyClosure = { Message message -> }) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageProducer messageProducer = session.createProducer(topic ? session.createTopic(destination) : session.createQueue(destination))
        TextMessage message = session.createTextMessage(messageText)
        propertyClosure(message)
        messageProducer.send(message)
    }

    String readMessage(String queue) {
        Connection connection = AMQ_CONNECTION_FACTORY.createConnection()
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        MessageConsumer consumer = session.createConsumer(session.createQueue(queue))
        connection.start()
        TextMessage message = consumer.receive(1000) as TextMessage
        return message?.text
    }
}