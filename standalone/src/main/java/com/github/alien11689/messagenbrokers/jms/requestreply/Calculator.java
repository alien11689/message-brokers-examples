package com.github.alien11689.messagenbrokers.jms.requestreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

@Slf4j
public class Calculator implements Runnable {
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public void run() {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createQueue("simple.adder"));
            final Session finalSession = session;
            consumer.setMessageListener(message -> {
                MapMessage mapMessage = (MapMessage) message;
                MessageProducer producer = null;
                try {
                    int a = mapMessage.getInt("a");
                    int b = mapMessage.getInt("b");
                    producer = finalSession.createProducer(message.getJMSReplyTo());
                    MapMessage reply = finalSession.createMapMessage();
                    reply.setInt("result", a + b);
                    producer.send(reply);
                } catch (JMSException e) {
                    log.error("Calculator error", e);
                } finally {
                    if (producer != null) {
                        try {
                            producer.close();
                        } catch (JMSException e) {
                            log.error("Cannot close producer", e);
                        }
                    }
                }

            });
        } catch (JMSException e) {
            log.error("Exception occured", e);
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException e) {
                    log.error("Cannot close consumer", e);
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
