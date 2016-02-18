package com.github.alien11689.messagenbrokers.amqp.requestreply;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Calculator implements Runnable {

    private static final ConnectionFactory connectionFactory = new ConnectionFactory();

    public void run() {
        connectionFactory.setHost("Localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");

        Connection connection = null;
        Channel channel = null;

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
            channel.queueDeclare("simple.adder", true, false, false, null);
            channel.basicConsume("simple.adder", true, queueingConsumer);
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery(60000);
            String correlationId = delivery.getProperties().getCorrelationId();
            String replyTo = delivery.getProperties().getReplyTo();
            String[] split = new String(delivery.getBody(), "UTF-8").split(",");
            int result = Integer.parseInt(split[0]) + Integer.parseInt(split[1]);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlationId).build();
            channel.basicPublish("", replyTo, properties, String.valueOf(result).getBytes("UTF-8"));
        } catch (IOException | TimeoutException | InterruptedException e) {
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
