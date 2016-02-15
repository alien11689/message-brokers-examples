package com.github.alien11689.messagenbrokers.jms.requestreply;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

@Slf4j
public class RequestReply {
    private static ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public static void main(String[] args) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            producer = session.createProducer(session.createQueue("simple.adder"));
            MapMessage message = session.createMapMessage();
            message.setInt("a", 5);
            message.setInt("b", 10);
            TemporaryQueue temporaryQueue = session.createTemporaryQueue();
            message.setJMSReplyTo(temporaryQueue);
            producer.send(message);
            consumer = session.createConsumer(temporaryQueue);
            connection.start();
            Message receive = consumer.receive(1000);
            MapMessage mapMessage = (MapMessage) receive;
            int result = mapMessage.getInt("result");
            System.out.println("Adding result is " + result);
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
