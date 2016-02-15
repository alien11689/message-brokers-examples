package com.github.alien11689.messagenbrokers.simple.amqp.requestreply;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RequestReply {

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
            String replyQueue = "responseQueue";
            channel.queueDeclare(replyQueue, true, false, false, null);
            String correlationId = UUID.randomUUID().toString();
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties
                .Builder()
                .replyTo(replyQueue)
                .correlationId(correlationId)
                .build();
            channel.queueDeclare("simple.adder", true, false, false, null);
            channel.basicPublish("", "simple.adder", basicProperties, "4,7".getBytes("UTF-8"));
            QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
            channel.basicConsume(replyQueue, true, queueingConsumer);
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery(60000);
            System.out.println("Correlation id = " + delivery.getProperties().getCorrelationId());
            System.out.println("Reponse = " + new String(delivery.getBody(), "UTF-8"));
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
