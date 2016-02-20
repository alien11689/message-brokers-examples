package com.github.alien11689.messagenbrokers.jms.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class Listener {

    @Autowired
    JmsTemplate jmsTemplate;

    @JmsListener(destination = "spring.in")
    void handleMessage(String message) {
        jmsTemplate.send("spring.out", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("Spring: " + message);
            }
        });
    }
}
