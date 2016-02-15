package com.github.alien11689.messagenbrokers.jms.queue;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Listener {
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
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            List<String> messages = new ArrayList<>();
            connection.start();
            consumer = session.createConsumer(session.createQueue("simple.send"));
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        try {
                            String text = textMessage.getText();
                            System.out.println("Received message: " + text);
                            messages.add(text);
                        } catch (JMSException e) {
                            log.error("Cannot read text from message", e);
                        }
                    } else {
                        throw new RuntimeException("No message");
                    }

                }
            });
            while (messages.size() < 1) {
                Thread.sleep(1000);
            }
            System.out.println("At least one message received");
        } catch (JMSException e) {
            log.error("Exception occured", e);
        } finally {
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
