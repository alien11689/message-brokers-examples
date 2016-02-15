package com.github.alien11689.messagenbrokers.amqp.simple;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Listen {

    private static final ConnectionFactory connectionFactory = new ConnectionFactory();

    public static void main(String[] args) throws InterruptedException {
        connectionFactory.setHost("Localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare("simple.send", true, false, false, null);
            List<String> messages = new ArrayList<>();
            channel.basicConsume("simple.send", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String text = new String(body, "UTF-8");
                    System.out.println("Received message " + text);
                    messages.add(text);
                }
            });
            while (messages.size() < 1) {
                Thread.sleep(1000);
            }
            System.out.println("At least one message received");
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
