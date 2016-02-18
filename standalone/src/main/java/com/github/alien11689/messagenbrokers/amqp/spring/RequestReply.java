package com.github.alien11689.messagenbrokers.amqp.spring;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class RequestReply {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "spring.rr.in")
    @SendTo("spring.rr.out")
    Message handleMessage(Message message) throws UnsupportedEncodingException {
        String newMessage = "Spring: " + new String(message.getBody(), "UTF-8");
        return new Message(
            newMessage.getBytes("UTF-8"), new MessageProperties()
        );
    }
}
