package com.github.alien11689.messagenbrokers.jms.queue;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY;

@Slf4j
public class Send {
    public static void main(String[] args) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = AMQ_CONNECTION_FACTORY.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            producer = session.createProducer(session.createQueue("simple.send"));
            TextMessage message = session.createTextMessage("Test1");
            message.setIntProperty("iteration", 2);
            producer.send(message);
        } catch (JMSException e) {
            log.error("Exception occured", e);
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    log.error("Cannot close producer", e);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    log.error("Cannot close session", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error("Cannot close connection", e);
                }
            }
        }
    }
}
