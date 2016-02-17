package com.github.alien11689.messagenbrokers.jms.topic;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

@Slf4j
public class Send {
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public static void main(String[] args) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            producer = session.createProducer(session.createTopic("simple.tpc.send"));
            producer.send(session.createTextMessage("Test1"));
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
