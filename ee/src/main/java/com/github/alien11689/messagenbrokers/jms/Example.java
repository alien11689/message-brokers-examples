package com.github.alien11689.messagenbrokers.jms;

//@MessageDriven(mappedName = "inputQueue", activationConfig = {
//    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")}
//)
//public class Example implements MessageListener {
//    @Resource(name = "jmsConnectionFactory")
//    private static ConnectionFactory connectionFactory;
//
//    @Resource(name = "outputQueue", type = Queue.class)
//    private static Queue queue;
//
//
//    @Override
//    public void onMessage(Message message) {
//        Connection connection = null;
//        Session session = null;
//        MessageProducer producer = null;
//        try {
//            connection = connectionFactory.createConnection();
//            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//            producer = session.createProducer(queue);
//            TextMessage newMessage = session.createTextMessage("Test EE " + ((TextMessage) message).getText());
//            producer.send(newMessage);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        } finally {
//            if (producer != null) {
//                try {
//                    producer.close();
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (session != null) {
//                try {
//                    session.close();
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/message")
@Stateless
public class Example {

    @Resource(name = "jmsConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(name = "outputQueue", type = Queue.class)
    private Queue queue;

    @POST
    public void doGet(String message) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            producer = session.createProducer(queue);
            TextMessage newMessage = session.createTextMessage(message);
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
