package com.github.alien11689.messagenbrokers.jms;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "tomee.in"),
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Queue")
})
public class Example implements MessageListener {

    @Resource(name = "jmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(name = "outputQueue", type = Queue.class)
    private Queue queue;

    public void onMessage(Message message) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            String text = ((TextMessage) message).getText();
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            producer = session.createProducer(queue);
            TextMessage newMessage = session.createTextMessage("Tomee: " + text);
            producer.send(newMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
