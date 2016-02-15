package com.github.alien11689.messagenbrokers.jms.queue;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Slf4j
public class Receive {
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public static void main(String[] args) {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createQueue("simple.send"));
            connection.start();
            Message message = consumer.receive(60000);
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
            }else{
                throw new RuntimeException("No message");
            }
        } catch (JMSException e) {
            log.error("Exception occured", e);
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
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
