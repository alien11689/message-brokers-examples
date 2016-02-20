package com.github.alien11689.messagenbrokers.jms.requestreply;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY;

@Slf4j
public class Calculator implements Runnable {
    public void run() {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = AMQ_CONNECTION_FACTORY.createConnection();
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
            while(true){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("Interupted when sleeping", e);
                }
            }
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
