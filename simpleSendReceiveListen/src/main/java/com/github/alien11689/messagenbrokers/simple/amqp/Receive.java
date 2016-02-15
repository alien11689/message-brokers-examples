package com.github.alien11689.messagenbrokers.simple.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Receive {

    private static final ConnectionFactory connectionFactory = new ConnectionFactory();

    public static void main(String[] args) {
        connectionFactory.setHost("Localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("simple.send", true, false, false, null);
            GetResponse getResponse = channel.basicGet("simple.send", true);
            System.out.println("Received message " + new String(getResponse.getBody(), "UTF-8"));
        } catch (IOException | TimeoutException e) {
            log.error("Exception occured", e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    log.error("Cannot close producer", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.error("Cannot close connection", e);
                }
            }
        }
    }
}
