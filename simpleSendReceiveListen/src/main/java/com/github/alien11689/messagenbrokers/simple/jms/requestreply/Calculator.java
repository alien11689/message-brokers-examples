package com.github.alien11689.messagenbrokers.simple.jms.requestreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

@Slf4j
public class Calculator {
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public static void main(String[] args) throws InterruptedException {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createQueue("simple.adder"));
            final Session finalSession = session;
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
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

                }
            });
            connection.start();
            Thread.sleep(60000);
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
