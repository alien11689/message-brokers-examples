package com.github.alien11689.messagenbrokers.jms.spring;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class RequestReply {
    @JmsListener(destination = "spring.rr.in", containerFactory = "jmsListenerContainerFactory")
    @SendTo("spring.rr.out")
    String handleMessage(String message) {
        return "Spring: " + message;
    }
}
